plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.0.0"
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":browser"))

    intellijPlatform {
        intellijIdeaCommunity("2024.1.4")
        bundledPlugins("ByteCodeViewer", "com.intellij.java", "org.jetbrains.kotlin")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "jclasslib"
    }
    sandboxContainer = rootProject.layout.buildDirectory.dir("../idea_sandbox")
}

tasks {
    patchPluginXml {
        changeNotes = """
            <ul>
                <li>Fixed "class root not found" message</li>
                <li>Support reading class files compiled by Java 19 and Java 20</li>
            </ul>
        """.trimIndent()
        version = project.version.toString()
    }

    publishPlugin {
        token = project.findProperty("intellij.publish.token")?.toString()
    }

    runIde {
        jvmArgs("-Xmx1g")
    }
}