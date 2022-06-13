plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.6.0"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2022.1")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "Kotlin"))
    sandboxDir.set("${rootProject.buildDir}/../idea_sandbox")
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            <ul>
                <li>Fixed exception when running with IDEA 2022.1</li>
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