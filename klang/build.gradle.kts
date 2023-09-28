plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
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
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
            languageVersion = "2.0"
        }


    }
}

