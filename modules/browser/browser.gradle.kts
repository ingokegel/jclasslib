plugins {
    kotlin("jvm")
    application
    `maven-publish`
}

configurePublishing()

application {
    mainClassName = "org.gjt.jclasslib.browser.BrowserApplication"
}

dependencies {
    api(project(":data"))
    compileOnly(":apple")
    implementation("com.install4j:install4j-runtime:8.0.5")
    implementation("org.jetbrains:annotations:13.0")
    implementation("org.jetbrains.kotlinx:kotlinx.dom:0.0.10")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("com.formdev:flatlaf:0.31")
}

tasks {
    val jar by existing(Jar::class) {
        archiveFileName.set("jclasslib-browser.jar")
        manifest {
            attributes("Main-Class" to application.mainClassName)
        }
    }

    val copyDist by registering(Copy::class) {
        dependsOn("jar")
        from(configurations.compileClasspath.map { it.files.filterNot { it.name.contains("install4j") } })
        from(jar)
        into(externalLibsDir)
    }

    register("dist") {
        dependsOn(copyDist)
    }
}

