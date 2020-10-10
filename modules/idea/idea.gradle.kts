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