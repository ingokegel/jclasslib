import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    idea
}

version = "5.2"
buildDir = file("build/gradle")

var mediaDir: File by extra
mediaDir = file("media")

var externalLibsDir: File by extra
externalLibsDir = file("$buildDir/externalLibs")

val kotlinVersion: String by extra

buildscript {
    val kotlinVersion by extra("1.1.50")
    val kotlinVersionParts = kotlinVersion.split('-')
    extra["kotlinVersionMain"] = kotlinVersionParts[0]

    val mavenUrls by extra(listOf("http://jcenter.bintray.com", "http://maven.ej-technologies.com/repository").map { java.net.URI(it) })

    repositories {
        flatDir {
            dirs = setOf(file("lib-compile"))
        }
        maven {
            url = java.net.URI("http://dl.bintray.com/jetbrains/intellij-plugin-service")
        }
        for (mavenUrl in mavenUrls) {
            maven {
                url = mavenUrl
            }
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.15")
    }
}

val mavenUrls: List<URI> by extra

subprojects {

    buildDir = File(rootProject.buildDir, path.substring(1).replace(':', '/'))

    group = "org.gjt.jclasslib"
    version = rootProject.version

    repositories {
        flatDir {
            dirs = setOf(file("lib"), file("$rootDir/lib-compile"))
        }
        for (mavenUrl in mavenUrls) {
            maven {
                url = mavenUrl
            }
        }
    }

    plugins.withType<JavaPlugin> {
        dependencies {
            add("testCompile", "org.testng:testng:6.8.8")
        }

        tasks.withType<JavaCompile>().forEach {compileJava ->
            compileJava.apply {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
            }
        }

        tasks.withType<Test>().forEach {test ->
            test.apply {
                useTestNG()
            }
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = "1.1"
            }
        }
    }
}

val clean by tasks.creating {
    doLast {
        delete(externalLibsDir)
    }
}

val dist by tasks.creating {}
val test by tasks.creating {}

tasks {
    "wrapper"(Wrapper::class) {
        gradleVersion = "4.0.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

gradle.projectsEvaluated {
    getTasksByName("clean", true).forEach { task ->
        if (task != clean) {
            clean.dependsOn(task)
        }
    }
    getTasksByName("dist", true).forEach { task ->
        if (task != dist) {
            dist.dependsOn(task)
        }
    }
    getTasksByName("test", true).forEach { task ->
        if (task != test) {
            test.dependsOn(task)
        }
    }
}

idea {
    module {
        name = "root"
        excludeDirs = files("build", "dist", "media").files + excludeDirs
    }
}