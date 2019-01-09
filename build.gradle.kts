import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.net.URI

plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "0.9.17" apply false
    idea
}

version = "5.3.1"
buildDir = file("build/gradle")

val kotlinVersion: String by project

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

    pluginManager.withPlugin("kotlin") {
        dependencies {
            add("compile", kotlin("stdlib", version = kotlinVersion))
            add("testCompile", "org.testng:testng:6.8.8")
        }

        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
        }

        tasks.withType<Test>().configureEach {
            useTestNG()
        }

        tasks.withType<KotlinJvmCompile>().configureEach {
            kotlinOptions {
                languageVersion = "1.3"
                apiVersion = "1.3"
                jvmTarget = "1.8"
            }
        }
    }
}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    register("dist") {
        dependsOn(":data:dist", ":browser:dist")
    }

    register<Delete>("clean") {
        dependsOn(":installer:clean", ":data:clean", ":browser:clean")
        delete(externalLibsDir)
    }
}

idea {
    module {
        name = "root"
        excludeDirs = files("build", "dist", "media").files + excludeDirs
    }
}