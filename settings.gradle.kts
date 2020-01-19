
pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        gradlePluginPortal()
        maven("https://maven.ej-technologies.com/repository")
        maven("https://jcenter.bintray.com/")
        maven("https://dl.bintray.com/jetbrains/intellij-plugin-service")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.jetbrains.kotlin") {
                useVersion(kotlinVersion)
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

