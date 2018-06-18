@file:Suppress("UnusedImport")

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

fun Project.configurePublishing() {
    pluginManager.apply("com.jfrog.bintray")
    val project = this
    val bintrayUser: String? by extra
    val bintrayApiKey: String? by extra

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
            from(project.the<JavaPluginConvention>().sourceSets["main"].allSource)
        }

        "publishToMavenLocal" {
            dependsOn("publishModulePublicationToMavenLocal", "jar")
        }

        publications {
            "Module"(MavenPublication::class) {
                from(project.components["java"])
                artifactId = "jclasslib-${project.name}"
                artifact(sourcesJar)
                pom {
                    licenses {
                        license {
                            name.set("GPL Version 2.0")
                            url.set("https://www.gnu.org/licenses/gpl-2.0.html")
                        }
                    }
                    developers {
                        developer {
                            name.set("Ingo Kegel")
                            url.set("https://github.com/ingokegel/jclasslib")
                            organization.set("ej-technologies GmbH")
                            organizationUrl.set("https://www.ej-technologies.com")
                        }
                    }
                }
            }
        }
    }

    configure<BintrayExtension> {
        if (bintrayUser != null && bintrayApiKey != null) {
            user = bintrayUser
            key = bintrayApiKey
            pkg(closureOf<BintrayExtension.PackageConfig> {
                repo = "maven"
                name = "jclasslib"
                setLicenses("GPL-2.0")
            })
            dryRun = project.hasProperty("dryRun")
            publish = true
            override = project.hasProperty("override")
        }
        setPublications("Module")
    }
}
