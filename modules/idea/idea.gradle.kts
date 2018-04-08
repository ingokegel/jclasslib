import org.jetbrains.intellij.tasks.PublishTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.2.17"
}

val kotlinVersion: String by project

dependencies {
    compile(project(":browser"))
    // explicit Kotlin dependency to prevent the intellij plugin from adding the Kotlin libraries in the lib directory
    compile(kotlin("stdlib"))
}

intellij {
    version = "IC-2017.1.5"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "org.jetbrains.kotlin:$kotlinVersion-release-IJ2017.1-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    "publishPlugin"(PublishTask::class) {
        setUsername(project.findProperty("intellij.publish.username") ?: "")
        setPassword(project.findProperty("intellij.publish.password") ?: "")
    }
}