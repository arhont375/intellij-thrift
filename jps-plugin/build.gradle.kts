plugins {
    id("org.jetbrains.intellij.platform")
    id("java")
}

tasks {
    jar {
        archiveFileName = "thrift-jps.jar"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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