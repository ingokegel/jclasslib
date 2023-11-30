import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder

plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
    `maven-publish`
}

configurePublishing()

dependencies {
    api(kotlin("stdlib"))
}

tasks {
    jar {
        archiveFileName.set("jclasslib-library.jar")
    }

    val copyDist by registering(Copy::class) {
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
        outputDirectory = layout.buildDirectory.map { it.dir("javadoc").asFile }
        applyDokkaConfig()
    }

    val doc by registering {
        dependsOn(dokkaHtml, dokkaJavadoc)
    }

    "javadocJar"(Jar::class) {
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc)
    }

    test {
        useTestNG()
        testLogging.showStandardStreams = true

        val majorVersions = listOf(8, 11, 17)
        for (majorVersion in majorVersions) {
            setJreSystemProperty(majorVersion)
        }
        systemProperty("majorVersions", majorVersions.joinToString(separator = ","))
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

fun Test.setJreSystemProperty(majorVersion: Int) {
    val javaHome = project.javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(majorVersion))
    }.get().metadata.installationPath.asFile.path

    systemProperty("javaHome.$majorVersion", javaHome)
}
