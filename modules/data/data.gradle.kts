@file:Suppress("RemoveRedundantBackticks")

import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
}

configurePublishing()

dependencies {
    compile(kotlin("stdlib"))
}

val publications: PublicationContainer = the<PublishingExtension>().publications
var externalLibsDir: File by rootProject.extra

tasks {
    val jar by getting(Jar::class) {
        archiveName = "jclasslib-library.jar"
    }

    val copyDist by creating(Copy::class) {
        dependsOn("jar")
        from(configurations.compile)
        from(jar.archivePath)
        into(externalLibsDir)
    }

    val dokka by getting(DokkaTask::class) {
        sourceDirs = setOf(file("src/main/kotlin"))
        includes = listOf("packages.md")
    }

    val dokkaJavadoc by creating(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    val doc by creating {
        dependsOn(dokka, dokkaJavadoc)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(dokkaJavadoc)
        classifier = "javadoc"
        from(dokkaJavadoc.outputDirectory)
    }

    publications {
        "Module"(MavenPublication::class) {
            artifact(mapOf("source" to javadocJar, "classifier" to "javadoc"))
        }
    }

    "dist" {
        dependsOn(doc, copyDist)
    }
}