import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.4.1"
}

val kotlinVersion: String by project

dependencies {
    compile(project(":browser"))
}

intellij {
    version = "IC-2018.3.2"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "org.jetbrains.kotlin:$kotlinVersion-release-IJ2018.3-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    publishPlugin {
        setUsername(project.findProperty("intellij.publish.username") ?: "")
        setPassword(project.findProperty("intellij.publish.password") ?: "")
    }

    runIde {
        jvmArgs("-Xmx1g")
    }
}