plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.2.1"
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":browser"))
}

intellij {
    version.set("IC-2020.2.4")
    pluginName.set("jclasslib")
    plugins.set(listOf("ByteCodeViewer", "java", "org.jetbrains.kotlin:202-$kotlinVersion-release-315-IJ8194.7"))
    sandboxDir.set("${rootProject.buildDir}/../idea_sandbox")
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            <ul>
                <li>Editing functionality for constant pool, attributes and bytecode</li>
                <li>UI fixes and improvements</li>
                <li>By setting the custom VM option -Djclasslib.locale=en (or another supported locale), the displayed language can be changed regardless of the default locale</li>
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