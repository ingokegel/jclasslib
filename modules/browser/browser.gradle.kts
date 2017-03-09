import org.gradle.api.java.archives.Manifest
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar

plugins {
    application
}

apply {
    plugin("kotlin")
    plugin("maven-publish")
}

application {
    mainClassName = "org.gjt.jclasslib.browser.BrowserApplication"
}

dependencies {
    compileOnly(":apple")
    compile("com.install4j:install4j-runtime:6.1.4")
    compile("org.jetbrains:annotations:13.0")
    compile("org.jetbrains.kotlinx:kotlinx.dom:0.0.10")
    compile("com.miglayout:miglayout-swing:5.0")
    compile(project(":data"))
}

tasks {
    val jar: Jar by tasks
    jar.apply {
        archiveName = "jclasslib-browser.jar"
        manifest(closureOf<Manifest> {
            attributes(mapOf("Main-Class" to the<ApplicationPluginConvention>().mainClassName))
        })
    }

    val copyDist by creating(Copy::class) {
        dependsOn("jar")
        from(configurations.compile.files.filterNot { it.name.contains("install4j") })
        from(jar.archivePath)
        into(rootProject.extra["externalLibsDir"])
    }

    "dist" {
        dependsOn(copyDist)
    }
}

