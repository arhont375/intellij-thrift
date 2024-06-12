plugins {
    id("java")
    id("org.jetbrains.intellij")
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
