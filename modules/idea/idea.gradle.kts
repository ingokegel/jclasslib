plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.0"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2020.2.4")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "org.jetbrains.kotlin:202-$kotlinVersion-release-283-IJ8194.7"))
    sandboxDir.set("${rootProject.buildDir}/../idea_sandbox")
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            <ul>
                <li>Value labels and hyperlinks in detail panes can be focused and text in value labels can be selected</li>
                <li>Support the Record class file attribute new in Java 14</li>
                <li>Support the PermittedSubclasses class file attribute new in Java 15</li>
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