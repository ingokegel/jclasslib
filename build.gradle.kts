import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "1.4.30" apply false
    idea
}

version = "5.8"
buildDir = file("build/gradle")

val kotlinVersion: String by project

subprojects {

    buildDir = File(rootProject.buildDir, path.substring(1).replace(':', '/'))

    group = "org.jclasslib"
    version = rootProject.version

    repositories {
        flatDir {
            dirs = setOf(file("lib"), file("$rootDir/lib-compile"))
        }
        maven("https://maven.ej-technologies.com/repository") {
            content {
                includeGroup("com.install4j")
            }
        }
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.ingokegel")
            }
        }
        maven("https://dl.bintray.com/kotlin/kotlinx") {
            content {
                includeGroup("org.jetbrains.kotlinx")
            }
        }
        mavenCentral()
    }

    pluginManager.withPlugin("kotlin") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        }

        dependencies {
            add("testImplementation", "org.testng:testng:6.8.8")
        }

        tasks.withType<Test>().configureEach {
            useTestNG()
        }

        val javaHome = the<JavaToolchainService>()
                .launcherFor(the<JavaPluginExtension>().toolchain)
                .get()
                .metadata
                .installationPath
                .toString()

        tasks.withType<KotlinJvmCompile>().configureEach {
            kotlinOptions {
                languageVersion = "1.4"
                apiVersion = "1.4"
                jvmTarget = "1.8"
                jdkHome = javaHome
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
        gradleVersion = "6.8.2"
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