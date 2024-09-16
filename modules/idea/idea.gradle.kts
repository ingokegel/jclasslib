plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

defaultTasks("buildPlugin")

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

val ideaVersion = "2024.2.1"
intellijPlatform {
    pluginConfiguration {
        name = "jclasslib"
        ideaVersion {
            untilBuild = provider { null }
        }
        changeNotes = """
            <ul>
                <li>Compatibility with IDEA 2024.2</li>
                <li>Support reading class files compiled by Java 21 and Java 22</li>
                <li>When saving modified class files, ask whether to overwrite the original class files or to save to a different output directory with the option to remember the selection</li>
            </ul>
        """.trimIndent()
    }
    pluginVerification {
        ides {
            ide(ideaVersion)
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
        instrumentationTools()
    }
}

tasks {
    runIde {
        jvmArgs("-Xmx1g")
    }
}