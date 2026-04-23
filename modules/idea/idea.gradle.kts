plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.12.0"
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
                <li>New "Find Subclasses", "Find Implementing Classes" and "Find Annotated Elements" actions</li>
                <li>Faster class file scanning for "Find usages"</li>
                <li>Support for reading Java 26 class files</li>
                <li>Various bug fixes</li>
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
        intellijIdea(ideaVersion)
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