import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    java
}

apply {
    plugin("kotlin")
    plugin("org.jetbrains.dokka")
    plugin("maven-publish")
}

val kotlinVersion = rootProject.extra["kotlinVersion"]
dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

val publications = the<PublishingExtension>().publications

tasks {
    val jar by getting(Jar::class) {
        archiveName = "jclasslib-library.jar"
    }

    val copyDist by creating(Copy::class) {
        dependsOn("jar")
        from(configurations.compile)
        from(jar.archivePath)
        into(rootProject.extra["externalLibsDir"])
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