apply plugin: 'kotlin'
apply plugin: 'org.jetbrains.dokka'

apply from: file('../publish.gradle')

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
}

apply plugin: 'java'

jar {
    archiveName = 'jclasslib-library.jar'
}

dokka {
    processConfigurations = ["compile"]
    includes = ['packages.md']
}

task dokkaJavadoc(type: org.jetbrains.dokka.gradle.DokkaTask) {
    outputFormat = 'javadoc'
    outputDirectory = "$buildDir/javadoc"
}

task javadocJar(type: Jar, dependsOn: dokkaJavadoc) {
    classifier = 'javadoc'
    from dokkaJavadoc.outputDirectory
}

publishing {
    publications {
        Module(MavenPublication) {
            artifact javadocJar {
                classifier "javadoc"
            }
        }
    }
}

task doc {
    dependsOn dokka, dokkaJavadoc
}

task copyDist(type: Copy, dependsOn: jar) {
    from configurations.compile
    from jar.archivePath
    into externalLibsDir
}

task dist {
    dependsOn doc, copyDist
}