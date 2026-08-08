plugins {
    kotlin("jvm")
    application
    id("com.vanniktech.maven.publish")
}

configurePublishing()

application {
    mainClass = "org.gjt.jclasslib.browser.BrowserApplication"
    applicationDefaultJvmArgs = findProperty("extraArgs")?.toString()?.split(" ") ?: emptyList()
}

val flatLafVersion = "3.7.1"

dependencies {
    api(project(":agent"))
    api(project(":data"))
    compileOnly(":apple")
    implementation("com.install4j:install4j-runtime:12.0.4")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.github.ingokegel:kotlinx.dom:0.0.10")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("com.formdev:flatlaf:$flatLafVersion")
    implementation("com.formdev:flatlaf-extras:$flatLafVersion")
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("com.github.ingokegel.assertj-swing:assertj-swing-junit-jupiter:3.18.2")
}

tasks {
    test {
        useJUnitPlatform()
        val launcher = project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(JAVA_RUN_VERSION))
            // the JetBrains runtime is required for reliable robot events in GUI tests
            if (project.hasProperty("jetbrains")) {
                vendor.set(JvmVendorSpec.JETBRAINS)
            }
        }
        executable = launcher.get().executablePath.asFile.absolutePath
        wrapWithXvfb()
    }
    jar {
        archiveFileName = "jclasslib-browser.jar"
        manifest {
            attributes("Main-Class" to application.mainClass.get())
        }
    }

    val copyDist = register<Copy>("copyDist") {
        from(configurations.compileClasspath.map { configuration -> configuration.files.filterNot { file -> file.name.contains("install4j") } })
        from(configurations.runtimeClasspath.map { configuration -> configuration.files.filter { file -> file.name.contains("svg") } })
        from(jar)
        into(externalLibsDir)
    }

    register("dist") {
        dependsOn(copyDist)
    }
}

