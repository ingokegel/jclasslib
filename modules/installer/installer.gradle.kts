import com.install4j.gradle.Install4jExtension
import com.install4j.gradle.Install4jTask

buildscript {
    dependencies {
        repositories {
            maven {
                setUrl("http://maven.ej-technologies.com/repository")
            }
        }
        classpath("com.install4j:install4j-gradle:6.1.1")
    }
}

apply {
    plugin("install4j")
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

    projectFile = file("resources/jclasslib.install4j")
    release = version as String
    disableSigning = !project.hasProperty("winCertPath") || !project.hasProperty("macCertPath")
    winKeystorePassword = (project.properties["winKeystorePassword"] ?: "") as String // TODO use property
    macKeystorePassword = (project.properties["macKeystorePassword"] ?: "") as String // TODO use property

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
