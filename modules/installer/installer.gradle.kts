import com.install4j.gradle.Install4jExtension
import com.install4j.gradle.Install4jTask

plugins {
    id("com.install4j.gradle") version "6.1.4"
}

val install4jHomeDir by project
val winCertPath by project
val macCertPath by project
val winKeystorePassword by project
val macKeystorePassword by project

configure<Install4jExtension> {
    if (install4jHomeDir != null) {
        installDir = file(install4jHomeDir)
    }
}

task("clean") {
    doLast {
        delete(rootProject.extra["mediaDir"])
    }
}

task<Install4jTask>("media") {
    dependsOn(":dist", ":clean")
    group = "Build"
    description = "Build all media files"

    projectFile = file("resources/jclasslib.install4j")
    release = version as String
    disableSigning = !project.hasProperty("winCertPath") || !project.hasProperty("macCertPath")
    winKeystorePassword = this@Installer_gradle.winKeystorePassword?.toString() ?: ""
    macKeystorePassword = this@Installer_gradle.macKeystorePassword?.toString() ?: ""

    variables = mapOf(
            "winCertPath" to (winCertPath ?: ""),
            "macCertPath" to (macCertPath ?: "")
    )

    doFirst {
        if (install4jHomeDir == null) {
            throw RuntimeException("Specify install4jHomeDir in gradle.properties and set it to an install4j installation directory")
        }
    }
}
