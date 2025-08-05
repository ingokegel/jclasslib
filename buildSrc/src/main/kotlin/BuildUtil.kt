import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension
import java.util.*

val Project.externalLibsDir: Provider<Directory> get() = rootProject.layout.buildDirectory.map { it.dir("externalLibs") }
val JAVA_RUN_VERSION get() = 17
val JAVA_COMPILE_VERSION get() = 11


// see https://github.com/vanniktech/gradle-maven-publish-plugin/pull/201#discussion_r584270633 for secret key export
//
// Required Gradle properties:
// signingInMemoryKey=
// signingInMemoryKeyPassword=
// mavenCentralUsername=
// mavenCentralPassword=

fun Project.configurePublishing() {
    configure<MavenPublishBaseExtension> {
        coordinates("org.jclasslib", "jclasslib-${project.name}", version.toString())
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()


        pom {
            name = "jclasslib bytecode viewer"
            description = "jclasslib bytecode viewer is a tool that visualizes all aspects of compiled Java class files and the contained bytecode."
            url = "https://github.com/ingokegel/jclasslib"

            scm {
                connection = "https://github.com/ingokegel/jclasslib"
                developerConnection = "https://github.com/ingokegel/jclasslib"
                url = "https://github.com/ingokegel/jclasslib"
            }
            licenses {
                license {
                    name = "GPL Version 2.0"
                    url = "https://www.gnu.org/licenses/gpl-2.0.html"
                }
            }
            developers {
                developer {
                    name = "Ingo Kegel"
                    url = "https://github.com/ingokegel/jclasslib"
                    organization = "ej-technologies GmbH"
                    organizationUrl = "https://www.ej-technologies.com"
                }
            }
            if (findProperty("signingInMemoryKey") == null) {
                configure<SigningExtension> {
                    useGpgCmd()
                }
            }
        }
    }
}

private fun String.capitalizeFirstCharacter() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
