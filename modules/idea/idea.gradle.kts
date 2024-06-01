plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.17.3"
}

dependencies {
    implementation(project(":browser"))
}

intellij {
    version = "IC-2022.2.4"
    pluginName = "jclasslib"
    plugins = listOf("ByteCodeViewer", "java", "Kotlin")
    sandboxDir = rootProject.layout.buildDirectory.dir("../idea_sandbox").get().asFile.path
    updateSinceUntilBuild = false
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