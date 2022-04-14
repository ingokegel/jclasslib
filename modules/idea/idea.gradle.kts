plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.5.2"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2022.1")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "Kotlin"))
    sandboxDir.set("${rootProject.buildDir}/../idea_sandbox")
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            <ul>
                <li>Improved display of record attributes by displaying each record entry as a single node with nested attribute nodes</li>
                <li>Support reading class files compiled by Java 18</li>
                <li>Added a placeholder panel with instructions in the IDEA tool window</li>
                <li>PermittedSubclassesAttribute and RecordAttribute were saved incorrectly and corrupted the class file</li>
            </ul>
        """.trimIndent())
        version.set(project.version.toString())
    }

    publishPlugin {
        token.set(project.findProperty("intellij.publish.token")?.toString())
    }

    runIde {
        jvmArgs("-Xmx1g")
    }
}