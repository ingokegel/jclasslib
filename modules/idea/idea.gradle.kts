plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.4.15"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version = "IC-2018.3.2"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "org.jetbrains.kotlin:$kotlinVersion-release-IJ2018.3-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    patchPluginXml {
        changeNotes("""
            <ul>
                <li>Improved method for finding class files, decompiled classes and Scala classes can now be shown</li>
                <li>Support NestHost and NestMembers attributes new in Java 11+</li>
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