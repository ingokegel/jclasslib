plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    // for local dokka in lib-compile
    //classpath ':dokka-fatjar'
    //classpath ':dokka-gradle-plugin'
    "compile"("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
    "compile"("org.jetbrains.kotlinx:kotlinx.dom:0.0.10")
}
