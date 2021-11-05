plugins {
    java
    `maven-publish`
}

configurePublishing()

sourceSets {
    create("java9") {
        java {
            setSrcDirs(listOf("src/main/java9"))
        }
    }
}

dependencies {
    add("java9Implementation", sourceSets.main.get().output)
}

tasks {
    jar {
        archiveFileName.set("jclasslib-agent.jar")
        manifest {
            attributes(
                    "Agent-Class" to "org.jclasslib.agent.AgentMain",
                    "Premain-Class" to "org.jclasslib.agent.AgentMain",
                    "Can-Redefine-Classes" to "true",
                    "Can-Retransform-Classes" to "true"
            )
        }
        from(sourceSets["java9"].output)
    }

    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    "compileJava9Java"(JavaCompile::class) {
        sourceCompatibility = "9"
        targetCompatibility = "9"
    }

    val copyDist by registering(Copy::class) {
        from(jar)
        into(externalLibsDir)
    }

    register("dist") {
        dependsOn(copyDist)
    }
}