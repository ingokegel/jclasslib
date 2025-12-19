import com.install4j.gradle.Install4jTask

plugins {
    id("com.install4j.gradle") version "12.0.2"
}

val macProvisioningProfile: String? by project
val appStoreCerts: String? by project
val appleIssuerId: String? by project
val appleKeyId: String? by project
val applePrivateApiKey: String? by project
val digestSigningCommandLine: String? by project

tasks {
    register<Delete>("clean") {
        delete(rootProject.file("media"))
    }

    register<Install4jTask>("media") {
        configureInstall4j()
        buildIds = listOf("2047307151", "2047307321", "2047307325", "850942491")
    }

    register<Install4jTask>("appstore") {
        configureInstall4j()
        buildIds = listOf("2047307322")
        variables.putAll(mapOf(
            "macProvisioningProfile" to (macProvisioningProfile ?: ""),
            "appStoreCerts" to (appStoreCerts ?: ""),
            "sys.ext.macKeySource" to "pkcs12"
        ))
    }
}


fun Install4jTask.configureInstall4j() {
    dependsOn(":dist")

    inputs.dir(rootProject.file("modules"))
    inputs.files(rootProject.file("license"))

    group = "Build"
    description = "Build all media files"

    projectFile = file("resources/jclasslib.install4j")
    release = version as String
    macKeystorePassword = ""

    if (appleIssuerId == null || appleKeyId == null || applePrivateApiKey == null) {
        disableNotarization = true
    }

    variables.putAll(mapOf(
        "digestSigningCommandLine" to (digestSigningCommandLine ?: ""),
        "appleIssuerId" to (appleIssuerId ?: ""),
        "appleKeyId" to (appleKeyId ?: ""),
        "applePrivateApiKey" to (applePrivateApiKey ?: ""),
    ))

}