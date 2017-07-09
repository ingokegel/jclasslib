import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayUploadTask
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import kotlinx.dom.*

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
    val kotlinVersion by extra("1.1.3-2")
    val kotlinVersionParts = kotlinVersion.split('-')
    extra["kotlinVersionMain"] = kotlinVersionParts[0]
    extra["kotlinVersionHotfix"] = if (kotlinVersionParts.size > 1) "-${kotlinVersionParts[1]}" else  ""

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

        // for local dokka in lib-compile
        //classpath ':dokka-fatjar'
        //classpath ':dokka-gradle-plugin'
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
        classpath("org.jetbrains.kotlinx:kotlinx.dom:0.0.10")
    }
}

val mavenUrls: List<URI> by extra

subprojects {

    val subProject = this

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

    // TODO modularize this again once GSK supports it
    plugins.withType<MavenPublishPlugin> {
        subProject.apply {
            plugin("com.jfrog.bintray")
        }

        val bintrayUser: String? by extra
        val bintrayApiKey: String? by extra

        configure<BintrayExtension> {
            if (bintrayUser != null && bintrayApiKey != null) {
                user = bintrayUser
                key = bintrayApiKey
                pkg(closureOf<BintrayExtension.PackageConfig> {
                    repo = "maven"
                    name = "jclasslib"
                    setLicenses("GPL-2.0")
                })
                dryRun = subProject.hasProperty("dryRun")
                publish = true
                override = subProject.hasProperty("override")
            }
            setPublications("Module")
        }

        val publications = the<PublishingExtension>().publications
        tasks {
            "bintrayUpload"(BintrayUploadTask::class) {
                doFirst {
                    if (bintrayUser == null || bintrayApiKey == null) {
                        throw RuntimeException("Specify bintrayUser and bintrayApiKey in gradle.properties")
                    }
                }
            }

            val sourcesJar by creating(Jar::class) {
                classifier = "sources"
                from(the<JavaPluginConvention>().sourceSets["main"].allSource)
            }

            "publishToMavenLocal" {
                dependsOn("publishModulePublicationToMavenLocal", "jar")
            }

            gradle.projectsEvaluated {
                publications {
                    "Module"(MavenPublication::class) {
                        from(subProject.components["java"])
                        artifactId = "jclasslib-${subProject.name}"
                        artifact(sourcesJar)
                        pom.withXml {
                            val dependencies = asElement().firstChildElement("dependencies")
                            dependencies?.childElements()
                                    ?.filter { it.firstChildElement("groupId")?.textContent == "com.install4j" }
                                    ?.forEach { dependencies.removeChild(it) }
                        }
                    }
                }
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