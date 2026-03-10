plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    implementation("org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin:2.1.0")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.36.0")

    // Workaround because of https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/2062
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}