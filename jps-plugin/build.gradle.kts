plugins {
    id("org.jetbrains.intellij.platform")
    id("java")
}

tasks {
    jar {
        archiveFileName = "thrift-jps.jar"
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(project.property("ideaVersion") as String)
        bundledPlugins("com.intellij.java")
        instrumentationTools()
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
