import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin) apply false
    idea
}

version = "6.0.5"
val rootBuildDir = file("build/gradle")
layout.buildDirectory = rootBuildDir

subprojects {
    layout.buildDirectory = File(rootBuildDir, path.substring(1).replace(':', '/'))

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
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") {
            content {
                includeGroup("org.jetbrains.kotlinx")
            }
        }
        mavenCentral()
    }

    pluginManager.withPlugin("kotlin") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(11)
                vendor = JvmVendorSpec.ADOPTIUM
            }
        }

        dependencies {
            add("testImplementation", "org.testng:testng:6.8.8")
        }

        tasks.withType<Test>().configureEach {
            useTestNG()
        }

        tasks.withType<KotlinJvmCompile>().configureEach {
            kotlinOptions {
                languageVersion = "1.9"
                apiVersion = "1.9"
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
        gradleVersion = "8.5"
        distributionType = Wrapper.DistributionType.ALL
    }

    register("dist") {
        dependsOn(":data:dist", ":browser:dist", ":agent:dist")
    }

    register<Delete>("clean") {
        dependsOn(":installer:clean", ":data:clean", ":browser:clean", ":agent:clean")
        delete(externalLibsDir)
    }
}

idea {
    module {
        name = "root"
        excludeDirs = files("build", "dist", "media").files + excludeDirs
    }
}
