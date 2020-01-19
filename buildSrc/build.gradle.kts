plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
}

configurations {
    all {
        resolutionStrategy.dependencySubstitution {
            all {
                (requested as? ModuleComponentSelector)?.let {
                    if (it.group == "org.apache.httpcomponents" && it.module == "httpclient") {
                        useTarget("org.apache.httpcomponents:httpclient:4.5.3", "Required by Gradle Intellij Plugin")
                    }
                }
            }
        }
    }
}

dependencies {
    // for local dokka in lib-compile
    //classpath ':dokka-fatjar'
    //classpath ':dokka-gradle-plugin'
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
}
