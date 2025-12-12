plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.10.0"
}

defaultTasks("buildPlugin")

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

val ideaVersion = "2025.2.5"
intellijPlatform {
    pluginConfiguration {
        name = "jclasslib"
        ideaVersion {
            untilBuild = provider { null }
        }
        changeNotes = """
            <ul>
                <li>Compatibility with Kotlin K2 mode</li>
            </ul>
        """.trimIndent()
    }
    pluginVerification {
        ides {
            create {
                version = ideaVersion
            }
        }
    }
    sandboxContainer = rootProject.layout.buildDirectory.dir("../idea_sandbox")
    projectName = "jclasslib"
}

dependencies {
    implementation(project(":browser"))

    intellijPlatform {
        intellijIdeaCommunity(ideaVersion)
        bundledPlugins("ByteCodeViewer", "com.intellij.java")

        pluginVerifier()
        zipSigner()
    }
}

tasks {
    runIde {
        jvmArgs("-Xmx1g")
    }
}