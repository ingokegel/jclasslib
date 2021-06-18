import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import java.io.File

val Project.externalLibsDir: File get() = file("${rootProject.buildDir}/externalLibs")

fun Project.configurePublishing() {
    pluginManager.apply("signing")
    val project = this
    val ossrhUser: String? by extra
    val ossrhPassword: String? by extra

    tasks {
        val sourcesJar by registering(Jar::class) {
            archiveClassifier.set("sources")
            from(project.the<JavaPluginExtension>().sourceSets["main"].allSource)
        }

        val javadocJar by registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        "publishToMavenLocal" {
            dependsOn("publishModulePublicationToMavenLocal", "jar")
        }

        create("publishToCentral") {
            dependsOn("publishModulePublicationToOssrhRepository")
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
                create<MavenPublication>("Module") {
                    from(project.components["java"])
                    artifactId = "jclasslib-${project.name}"
                    artifact(sourcesJar.get())
                    artifact(javadocJar.get())
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
                sign(publications["Module"])
            }
        }
    }
}
