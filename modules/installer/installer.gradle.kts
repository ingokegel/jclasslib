import com.install4j.gradle.Install4jTask

plugins {
    id("com.install4j.gradle") version "12.0.4"
}

val winCertPath = project.findProperty("winCertPath") as String?
val macCertPath = project.findProperty("macCertPath") as String?
val macProvisioningProfile = project.findProperty("macProvisioningProfile") as String?
val appStoreCerts = project.findProperty("appStoreCerts") as String?
val appleIssuerId = project.findProperty("appleIssuerId") as String?
val appleKeyId = project.findProperty("appleKeyId") as String?
val applePrivateApiKey = project.findProperty("applePrivateApiKey") as String?
val digestSigningCommandLine = project.findProperty("digestSigningCommandLine") as String?

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
        "winCertPath" to (winCertPath ?: ""),
        "macCertPath" to (macCertPath ?: ""),
        "appStoreCerts" to (appStoreCerts ?: ""),
        "digestSigningCommandLine" to (digestSigningCommandLine ?: ""),
        "appleIssuerId" to (appleIssuerId ?: ""),
        "appleKeyId" to (appleKeyId ?: ""),
        "applePrivateApiKey" to (applePrivateApiKey ?: ""),
    ))

}