
plugins {
	alias(libs.plugins.kotlinMultiplatform).apply(false)
}


allprojects {

    repositories {
		mavenLocal()
        mavenCentral()
    }

	group = "io.ygdrasil"
	version = "1.0.0-SNAPSHOT"
}


