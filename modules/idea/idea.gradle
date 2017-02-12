plugins {
  id "org.jetbrains.intellij" version "0.2.1"
}

apply plugin: 'kotlin'

intellij {
    version 'IC-2016.2.5'
    pluginName 'jclasslib'
    plugins 'ByteCodeViewer', "org.jetbrains.kotlin:$kotlinVersion-IJ2016.2-1@EAP-1.1"
    sandboxDirectory "$rootProject.buildDir/../idea_sandbox"
    updateSinceUntilBuild false

    publish {
        username=project.findProperty("intellij.publish.username") ?: ''
        password=project.findProperty("intellij.publish.password") ?: ''
    }
}

dependencies {
    compile project(':browser')
    // explicit Kotlin dependency to prevent the intellij plugin from adding the Kotlin libraries in the lib directory
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
}

clean {
    doLast {
        delete file("${intellij.sandboxDirectory}/plugins")
    }
}