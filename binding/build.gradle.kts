
plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

allprojects {

    repositories {
        mavenCentral()
    }

	group = "io.ygdrasil"
	version = "1.0.0-SNAPSHOT"
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

