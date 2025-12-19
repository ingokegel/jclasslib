plugins {
    kotlin("jvm")
    application
    id("com.vanniktech.maven.publish")
}

configurePublishing()

application {
    mainClass = "org.gjt.jclasslib.browser.BrowserApplication"
}

val flatLafVersion = "2.6"

dependencies {
    api(project(":agent"))
    api(project(":data"))
    compileOnly(":apple")
    implementation("com.install4j:install4j-runtime:12.0.2")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.github.ingokegel:kotlinx.dom:0.0.10")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("com.formdev:flatlaf:$flatLafVersion")
    implementation("com.formdev:flatlaf-extras:$flatLafVersion")
}

tasks {
    jar {
        archiveFileName = "jclasslib-browser.jar"
        manifest {
            attributes("Main-Class" to application.mainClass.get())
        }
    }

    val copyDist by registering(Copy::class) {
        from(configurations.compileClasspath.map { configuration -> configuration.files.filterNot { file -> file.name.contains("install4j") } })
        from(configurations.runtimeClasspath.map { configuration -> configuration.files.filter { file -> file.name.contains("svg") } })
        from(jar)
        into(externalLibsDir)
    }

    register("dist") {
        dependsOn(copyDist)
    }
}

