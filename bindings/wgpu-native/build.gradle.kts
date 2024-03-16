
plugins {
	kotlin("jvm") version libs.versions.kotlin
	id("com.gradle.plugin-publish") version "1.0.0"
}


allprojects {

    repositories {
		mavenLocal()
        mavenCentral()
    }

	group = "io.ygdrasil"
	version = "1.0.0-SNAPSHOT"
}


