import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.2.17"
}

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

dependencies {
    compile(project(":browser"))
    // explicit Kotlin dependency to prevent the intellij plugin from adding the Kotlin libraries in the lib directory
    compile(kotlin("stdlib"))
}

intellij {
    version = "IC-2016.3.7"
    pluginName = "jclasslib"
    setPlugins("ByteCodeViewer", "org.jetbrains.kotlin:$kotlinVersion-release-IJ2016.3-1")
    sandboxDirectory = "${rootProject.buildDir}/../idea_sandbox"
    updateSinceUntilBuild = false
}

tasks {
    "publishPlugin"(PublishTask::class) {
        setUsername(project.findProperty("intellij.publish.username") ?: "")
        setPassword(project.findProperty("intellij.publish.password") ?: "")
    }
}