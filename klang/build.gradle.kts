plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

val projectVersion = System.getenv("VERSION")
	?.takeIf { it.isNotBlank() }
	?: "0.0.0"

allprojects {

    repositories {
        mavenCentral()
    }

	group = "io.ygdrasil"
	version = projectVersion

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

