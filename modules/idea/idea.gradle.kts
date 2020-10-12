plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.5.0"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version = "IC-2019.2.4"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "java", "org.jetbrains.kotlin:$kotlinVersion-release-IJ2019.2-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    patchPluginXml {
        changeNotes("""
            <ul>
                <li>Value labels and hyperlinks in detail panes can be focused and text in value labels can be selected</li>
                <li>Support the Record class file attribute new in Java 14</li>
                <li>Support the PermittedSubclasses class file attribute new in Java 15</li>
            </ul>
        """.trimIndent())
        version(project.version)
    }

    publishPlugin {
        setUsername(project.findProperty("intellij.publish.username") ?: "")
        setPassword(project.findProperty("intellij.publish.password") ?: "")
    }

    runIde {
        jvmArgs("-Xmx1g")
    }
}