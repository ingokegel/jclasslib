@file:Suppress("UnusedImport")

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayUploadTask
import kotlinx.dom.childElements
import kotlinx.dom.firstChildElement
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

        gradle.projectsEvaluated {
            publications {
                "Module"(MavenPublication::class) {
                    from(project.components["java"])
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
