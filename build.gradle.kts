import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.net.URI

plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "0.9.17" apply false
    idea
}

version = "5.3"
buildDir = file("build/gradle")

var mediaDir: File by extra(file("media"))
var externalLibsDir: File by extra(file("$buildDir/externalLibs"))

subprojects {

    buildDir = File(rootProject.buildDir, path.substring(1).replace(':', '/'))

    group = "org.gjt.jclasslib"
    version = rootProject.version

    repositories {
        flatDir {
            dirs = setOf(file("lib"), file("$rootDir/lib-compile"))
        }
        jcenter()
        maven("http://maven.ej-technologies.com/repository")
    }

    plugins.withId("kotlin") {
        dependencies {
            add("testCompile", "org.testng:testng:6.8.8")
        }

        tasks.withType<JavaCompile> {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
        }

        tasks.withType<Test> {
            useTestNG()
        }

        tasks.withType<KotlinJvmCompile> {
            kotlinOptions {
                languageVersion = "1.2"
                apiVersion = "1.2"
                jvmTarget = "1.8"
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
    getByName<Wrapper>("wrapper") {
        gradleVersion = "4.10-rc-2"
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