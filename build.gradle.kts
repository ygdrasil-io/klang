plugins {
    kotlin("jvm") version "1.9.0-Beta"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
	id("org.sonarqube") version "4.1.0.3113"
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
                sourceCompatibility = JavaVersion.VERSION_20
                targetCompatibility = JavaVersion.VERSION_20
            }
            languageVersion = "2.0"
        }
    }
}

sonarqube {
	properties {
		property("sonar.projectKey", "ygdrasil-io_klang")
		property("sonar.organization", "ygdrasil-io")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}