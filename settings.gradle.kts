import org.gradle.toolchains.foojay.FoojayToolchainResolver

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.ej-technologies.com/repository") {
            content {
                includeGroup("com.install4")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver") version("0.4.0")
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("foojay") {
                resolverClass.set(FoojayToolchainResolver::class.java)
            }
        }
    }
}

file("modules").listFiles()!!
        .filter { it.isDirectory && !it.name.startsWith('.') && File(it, it.name + ".gradle.kts").exists() }
        .forEach { dir ->
            include(dir.name)
            findProject(":${dir.name}")?.apply {
                projectDir = dir
                buildFileName = "${dir.name}.gradle.kts"
            }
        }

