import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    idea
}

version = "6.1.0.3"
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
                languageVersion = JavaLanguageVersion.of(JAVA_COMPILE_VERSION)
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
            compilerOptions {
                languageVersion = KotlinVersion.KOTLIN_2_0
                apiVersion = KotlinVersion.KOTLIN_2_0
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
        gradleVersion = "9.1.0"
        distributionType = Wrapper.DistributionType.ALL
    }

    register("dist") {
        dependsOn(":data:dist", ":browser:dist", ":agent:dist")
    }

    updateDaemonJvm {
        languageVersion = JavaLanguageVersion.of(JAVA_COMPILE_VERSION)
    }
}

idea {
    module {
        name = "root"
        excludeDirs = files("build", "dist", "media").files + excludeDirs
    }
}
