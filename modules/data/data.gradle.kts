import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradleDokkaSourceSet

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

    dokkaHtml {
        applyDokkaConfig {
            includes = listOf("packages.md")
        }
    }

    dokkaJavadoc {
        outputDirectory = "$buildDir/javadoc"
        applyDokkaConfig()
    }

    val doc by registering {
        dependsOn(dokkaHtml, dokkaJavadoc)
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

fun DokkaTask.applyDokkaConfig(additionalConfig: GradleDokkaSourceSet.() -> Unit =  {}) {
    dokkaSourceSets {
        configureEach {
            moduleDisplayName = "jclasslib data"
            additionalConfig()
        }
    }
}