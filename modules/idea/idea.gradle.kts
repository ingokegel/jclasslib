import org.jetbrains.intellij.tasks.PublishTask

plugins {
    id("org.jetbrains.intellij") version "0.2.13"
}

apply {
    plugin("kotlin")
}

val kotlinVersion = rootProject.extra["kotlinVersion"]
val kotlinVersionMain = rootProject.extra["kotlinVersionMain"]
dependencies {
    compile(project(":browser"))
    // explicit Kotlin dependency to prevent the intellij plugin from adding the Kotlin libraries in the lib directory
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

intellij {
    version = "IC-2016.3.7"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "org.jetbrains.kotlin:$kotlinVersionMain-release-IJ2016.3-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    "publishPlugin"(PublishTask::class) {
        setUsername(project.findProperty("intellij.publish.username") ?: "")
        setPassword(project.findProperty("intellij.publish.password") ?: "")
    }
}