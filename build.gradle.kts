import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "0.9.18" apply false
    idea
}

version = "5.3.2"
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

    apply(plugin = "idea")
    configure<IdeaModel> {
        module {
            inheritOutputDirs = true
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.4.1"
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