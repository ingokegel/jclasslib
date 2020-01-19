import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
}

configurePublishing()

dependencies {
    api(kotlin("stdlib"))
}

tasks {
    val jar by existing(Jar::class) {
        archiveFileName.set("jclasslib-library.jar")
    }

    val copyDist by registering(Copy::class) {
        dependsOn(jar)
        from(configurations.compileClasspath)
        from(jar)
        into(externalLibsDir)
    }

    val dokka by existing(DokkaTask::class) {
        configuration {
            includes = listOf("packages.md")
        }
    }

    val dokkaJavadoc by registering(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    val doc by registering {
        dependsOn(dokka, dokkaJavadoc)
    }

    val javadocJar by registering(Jar::class) {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc)
    }

    publishing {
        publications {
            named<MavenPublication>("Module") {
                artifact(javadocJar.get()) {
                    classifier = "javadoc"
                }
            }
        }
    }

    register("dist") {
        dependsOn(doc, copyDist)
    }
}