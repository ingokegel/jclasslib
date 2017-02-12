apply plugin: 'kotlin'
apply plugin: 'application'

apply from: file('../publish.gradle')

mainClassName = 'org.gjt.jclasslib.browser.BrowserApplication'

dependencies {
    compileOnly ':apple'
    compile 'com.install4j:install4j-runtime:6.1.4'
    compile 'org.jetbrains:annotations:13.0'
    compile 'org.jetbrains.kotlinx:kotlinx.dom:0.0.10'
    compile 'com.miglayout:miglayout-swing:5.0'
    compile project(':data')
}

jar {
    archiveName = 'jclasslib-browser.jar'
    manifest {
        attributes("Main-Class": mainClassName)
    }
}

task copyDist(type: Copy, dependsOn: jar) {
    from configurations.compile.files.findAll {!it.name.contains('install4j')}
    from jar.archivePath
    into externalLibsDir
}

task dist {
    dependsOn copyDist
}


