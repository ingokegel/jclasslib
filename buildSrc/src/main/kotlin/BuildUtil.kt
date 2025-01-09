import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import java.util.*

val Project.externalLibsDir: Provider<Directory> get() = rootProject.layout.buildDirectory.map { it.dir("externalLibs") }
val Project.JAVA_RUN_VERSION get() = 17
val Project.JAVA_COMPILE_VERSION get() = 11

fun Project.configurePublishing(multiplatform: Boolean = false) {
    pluginManager.apply("signing")
    val project = this
    val ossrhUser: String? by extra
    val ossrhPassword: String? by extra

    tasks {
        if (!multiplatform) {
            register<Jar>("sourcesJar") {
                archiveClassifier.set("sources")
                from(project.the<JavaPluginExtension>().sourceSets["main"].allSource)
            }
        }

        "publishToMavenLocal" {
            dependsOn(if (multiplatform) "publishKotlinMultiplatformPublicationToMavenLocal" else "publishJvmPublicationToMavenLocal")
        }

        register("publishToCentral") {
            dependsOn("publishAllPublicationsToOssrhRepository")
        }

        configure<PublishingExtension> {
            repositories {
                maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                    name = "ossrh"
                    credentials {
                        username = ossrhUser
                        password = ossrhPassword
                    }
                }
            }

            publications {
                if (!multiplatform) {
                    create<MavenPublication>("jvm") {
                        from(components["java"])
                        artifactId = "jclasslib-${project.name}"
                        artifact(tasks.findByName("sourcesJar"))
                    }
                }

                publications.withType<MavenPublication>().all {
                    val artifactName =
                        "jclasslib-${project.name}" + if (multiplatform && this.name != "kotlinMultiplatform") {
                            "-${this.name}"
                        } else {
                            ""
                        }
                    gradle.projectsEvaluated {
                        // Must be done later, otherwise the default artifact names are used
                        artifactId = artifactName
                    }
                    val javadocJarTask =
                        tasks.register<Jar>("javadocJar" + if (multiplatform && name != "kotlinMultiplatform") name.capitalizeFirstCharacter() else "") {
                            archiveClassifier.set("javadoc")
                            archiveBaseName = artifactName
                        }
                    artifact(javadocJarTask.get())
                    pom {
                        name.set("jclasslib bytecode viewer")
                        description.set("jclasslib bytecode viewer is a tool that visualizes all aspects of compiled Java class files and the contained bytecode.")
                        url.set("https://github.com/ingokegel/jclasslib")

                        scm {
                            connection.set("https://github.com/ingokegel/jclasslib")
                            developerConnection.set("https://github.com/ingokegel/jclasslib")
                            url.set("https://github.com/ingokegel/jclasslib")
                        }
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
            configure<SigningExtension> {
                useGpgCmd()
                sign(publications)
            }
        }
    }
}

private fun String.capitalizeFirstCharacter() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
