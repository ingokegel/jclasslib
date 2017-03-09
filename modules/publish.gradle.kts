//TODO As of 0.7.0-SNAPSHOT this cannot be modularized in GSK
// also: Common classpath in root project or buildSrc is unavailable

apply plugin: 'com.jfrog.bintray'
apply plugin: 'maven-publish'

bintray {
    if (project.hasProperty('bintrayUser')) {
        user = bintrayUser
        key = bintrayApiKey
        pkg {
            repo = 'maven'
            name = "jclasslib"
            licenses = ['GPL-2.0']
        }
        dryRun = project.hasProperty('dryRun')
        publish = true
        override = project.hasProperty('override')
    }
}

bintrayUpload {
    doFirst {
        if (!project.hasProperty('bintrayUser')) {
            throw new RuntimeException('Specify bintrayUser and bintrayApiKey in gradle.properties')
        }
    }
}

task sourcesJar(type: Jar, dependsOn: classes) {
    classifier = 'sources'
    from sourceSets.main.allSource
}

publishing {
    publications {
        Module(MavenPublication) {
            from components.java
            artifactId "jclasslib-${project.name}"
            artifact sourcesJar {
                classifier "sources"
            }
            pom.withXml {
                asNode().find { it.name().getLocalPart() == 'dependencies' }.children().removeAll() {
                    it.groupId.text() == 'com.install4j'
                }
            }
        }
    }
}

bintray {
    publications = ['Module']
}