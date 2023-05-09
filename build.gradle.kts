plugins {
    kotlin("jvm") version "1.8.21"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(17)

    sourceSets.all {
        languageSettings {
            java {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            languageVersion = "2.0"
        }
    }
}
