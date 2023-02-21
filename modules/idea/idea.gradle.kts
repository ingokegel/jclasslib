plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.13.0"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2022.2.4")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "Kotlin"))
    sandboxDir.set("${rootProject.buildDir}/../idea_sandbox")
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