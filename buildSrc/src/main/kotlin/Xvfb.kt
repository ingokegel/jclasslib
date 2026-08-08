import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

private const val DEFAULT_SCREEN = "1920x1200x24"
private const val DEFAULT_WM = "metacity"

private fun screenOf(project: Project): String =
    project.findProperty("xvfbScreen")?.toString() ?: DEFAULT_SCREEN

private fun wmOf(project: Project): String? =
    (project.findProperty("xvfbWm")?.toString() ?: DEFAULT_WM).takeIf { it.isNotBlank() }

private fun isIdeaActive() = System.getProperty("idea.active") != null

private fun isWindows() = System.getProperty("os.name").lowercase().startsWith("windows")

private fun isMacos() = System.getProperty("os.name").lowercase().startsWith("mac")

// Wrap tests in xvfb-run so they can run without a real display.
// Auto-enabled on Linux when DISPLAY is unset and both xvfb-run and the configured WM
// (metacity by default) are on PATH; pass -Pxvfb to force on, -Pxvfb=false to force off.
fun Project.shouldUseXvfb(): Boolean {
    if (isIdeaActive() || isWindows() || isMacos()) {
        return false
    }
    val explicit = findProperty("xvfb")?.toString()
    if (explicit != null) {
        return explicit.toBoolean() || explicit.isEmpty()
    }
    val displaySet = providers.environmentVariable("DISPLAY")
        .map { it.isNotEmpty() }.getOrElse(false)
    if (displaySet) {
        return false
    }
    // Both xvfb-run and the configured WM (if any) must be on PATH.
    val commands = listOfNotNull("xvfb-run", wmOf(this))
    return commands.all { command ->
        providers.exec {
            commandLine("sh", "-c", "command -v $command")
            isIgnoreExitValue = true
        }.result.get().exitValue == 0
    }
}

fun Test.wrapWithXvfb() {
    val project = this.project
    if (!project.shouldUseXvfb()) {
        return
    }
    val screen = screenOf(project)
    val wm = wmOf(project)
    val nonReparenting = project.hasProperty("xvfbNonReparenting")
    val originalExecutable = this.executable
    val wrapperPath = project.layout.buildDirectory.get().asFile.resolve("xvfb/${this.name}-jdk/bin/java").also {
        it.parentFile.mkdirs()
    }
    val wmBlock = if (wm != null) """
        if ! command -v $wm >/dev/null 2>&1; then
            echo "ERROR: $wm not found. Install it or omit -PxvfbWm." >&2
            exit 1
        fi
        $wm >/dev/null 2>&1 &
        sleep 0.5
        """.trimIndent() else ""
    val envBlock = if (nonReparenting) "export _JAVA_AWT_WM_NONREPARENTING=1\n" else ""
    wrapperPath.writeText(
        """
        #!/bin/bash
        if ! command -v xvfb-run >/dev/null 2>&1; then
            echo "ERROR: xvfb-run not found. Install with: sudo apt install xvfb" >&2
            exit 1
        fi
        exec xvfb-run --auto-servernum --server-args="-screen 0 $screen" bash -c '
        $envBlock
        $wmBlock
        exec "${'$'}@"
        ' bash "$originalExecutable" "${'$'}@"
        """.trimIndent() + "\n"
    )
    wrapperPath.setExecutable(true)
    this.executable = wrapperPath.absolutePath
    this.logger.lifecycle("[${this.name}] Wrapping JVM in xvfb-run${wm?.let { " + $it" } ?: ""} (screen $screen)")
}
