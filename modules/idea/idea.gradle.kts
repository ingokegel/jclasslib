plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.13.0"
}

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2022.2.4")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "Kotlin"))
    sandboxDir = rootProject.layout.buildDirectory.dir("../idea_sandbox").get().asFile.path
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            <ul>
                <li>Fixed "class root not found" message</li>
                <li>Support reading class files compiled by Java 19 and Java 20</li>
            </ul>
        """.trimIndent())
        version.set(project.version.toString())
    }

    publishPlugin {
        token.set(project.findProperty("intellij.publish.token")?.toString())
    }

    runIde {
        jvmArgs("-Xmx1g")
    }
}