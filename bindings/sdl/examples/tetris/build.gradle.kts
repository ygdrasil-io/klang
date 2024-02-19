
plugins {
	kotlin("jvm") version libs.versions.kotlin
}

dependencies {
	api(project(":sdl4k"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}


