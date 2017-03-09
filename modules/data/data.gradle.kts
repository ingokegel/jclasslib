import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

apply {
    plugin("kotlin")
    plugin("org.jetbrains.dokka")
    from(file("../publish.gradle"))
}

val kotlinVersion = rootProject.extra["kotlinVersion"]
dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

val publications = the<PublishingExtension>().publications

tasks {
    val jar: Jar by tasks
    jar.apply {
        archiveName = "jclasslib-library.jar"
    }

    val copyDist by creating(Copy::class) {
        dependsOn("jar")
        from(configurations.compile)
        from(jar.archivePath)
        into(rootProject.extra["externalLibsDir"])
    }

    val dokka: DokkaTask by tasks
    dokka.apply {
        processConfigurations = listOf("compile")
        includes = listOf("packages.md")
    }

    val dokkaJavadoc by creating(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    val doc by creating {
        dependsOn(dokka, dokkaJavadoc)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(dokkaJavadoc)
        classifier = "javadoc"
        from(dokkaJavadoc.outputDirectory)
    }

    publications {
        "Module"(MavenPublication::class) {
            artifact(mapOf("source" to javadocJar, "classifier" to "javadoc"))
        }
    }

    "dist" {
        dependsOn(doc, copyDist)
    }
}