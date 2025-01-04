import com.install4j.gradle.Install4jTask

plugins {
    id("com.install4j.gradle") version "10.0.5"
}

val install4jHomeDir: String? by project
val macCertPath: String? by project
val winKeystorePassword: String? by project
val macKeystorePassword: String? by project
val macProvisioningProfile: String? by project
val appStoreOnly = project.findProperty("appStoreOnly").toString().toBoolean()
val appleId: String? by project
val appleIdPassword: String? by project
val azureVaultUri: String? by project
val azureTenantId: String? by project
val azureClientId: String? by project
val azureCertificateName: String? by project

install4j {
    install4jHomeDir?.let {
        installDir = file(it)
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.file("media"))
    }

    val externalWinKeystorePassword = winKeystorePassword
    val externalMacKeystorePassword = macKeystorePassword
    val externalAppleId = appleId
    val externalAppleIdPassword = appleIdPassword

    register<Install4jTask>("media") {
        dependsOn(":dist", ":clean")
        group = "Build"
        description = "Build all media files"

        projectFile = file("resources/jclasslib.install4j")
        release = version as String
        disableSigning = !project.hasProperty("macCertPath")
        winKeystorePassword = externalWinKeystorePassword ?: ""
        macKeystorePassword = externalMacKeystorePassword ?: ""
        appleId = externalAppleId ?: ""
        appleIdPassword = externalAppleIdPassword ?: ""

        variables = mapOf(
                "azureVaultUri" to (azureVaultUri ?: ""),
                "azureTenantId" to (azureTenantId ?: ""),
                "azureClientId" to (azureClientId ?: ""),
                "azureCertificateName" to (azureCertificateName ?: ""),
                "macCertPath" to (macCertPath ?: ""),
                "macProvisioningProfile" to (macProvisioningProfile ?: "")
        )

        if (appStoreOnly) {
            buildIds = listOf("2047307322")
        }

        doFirst {
            if (install4jHomeDir == null) {
                throw RuntimeException("Specify install4jHomeDir in gradle.properties and set it to an install4j installation directory")
            }
        }
    }
}
