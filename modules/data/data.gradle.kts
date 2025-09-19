import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

configurePublishing()

dependencies {
    commonTestImplementation(kotlin("test"))
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(JAVA_COMPILE_VERSION)
        vendor = JvmVendorSpec.ADOPTIUM
    }

    jvm {
    }

    js {
        nodejs()
    }
}


dokka {
    moduleName = "jclasslib data"
    dokkaSourceSets.named("jvmMain") {
        includes.from("packages.md")
    }
}

tasks {
    val copyDist by registering(Copy::class) {
        from(kotlin.jvm().compilations["main"].compileDependencyFiles)
        from("jvmJar")
        into(externalLibsDir)
    }

    val doc by registering {
        dependsOn(dokkaGenerate)
    }

    val dist by registering {
        dependsOn(doc, copyDist)
    }

    named<Test>("jvmTest") {
        javaLauncher.set(
            project.javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(JAVA_RUN_VERSION))
            }
        )
        useJUnitPlatform()
        val majorVersions = listOf(8, 11, 17)
        for (majorVersion in majorVersions) {
            setJreSystemProperty(majorVersion)
        }
        systemProperty("majorVersions", majorVersions.joinToString(separator = ","))
    }

    val compileTestJavaJvm by registering(JavaCompile::class) {
        javaCompiler.set(
            project.javaToolchains.compilerFor {
                languageVersion.set(JavaLanguageVersion.of(JAVA_RUN_VERSION))
            }
        )
        source(kotlin.sourceSets["jvmTest"].kotlin.sourceDirectories.map {
            fileTree(it) {
                include("**/*.java")
            }
        })

        val compileTestKotlinJvm by getting(KotlinCompile::class)
        destinationDirectory = compileTestKotlinJvm.destinationDirectory.get().asFile.parentFile.resolve("java")
        classpath = compileTestKotlinJvm.classpathSnapshotProperties.classpathSnapshot
    }

    named("jvmTestClasses") {
        dependsOn(compileTestJavaJvm)
    }
}

fun Test.setJreSystemProperty(majorVersion: Int) {
    val javaHome = project.javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(majorVersion)
    }.get().metadata.installationPath.asFile.path

    systemProperty("javaHome.$majorVersion", javaHome)
}
