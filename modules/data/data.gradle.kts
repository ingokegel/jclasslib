import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder

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
            includes.from("packages.md")
        }
    }

    dokkaJavadoc {
        outputDirectory.set(buildDir.resolve("javadoc"))
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

fun DokkaTask.applyDokkaConfig(additionalConfig: GradleDokkaSourceSetBuilder.() -> Unit =  {}) {
    dokkaSourceSets {
        configureEach {
            moduleName.set("jclasslib data")
            additionalConfig()
        }
    }
}