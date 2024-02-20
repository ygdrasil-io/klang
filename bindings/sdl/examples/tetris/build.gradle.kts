
plugins {
	kotlin("jvm") version libs.versions.kotlin
	application
}

dependencies {
	api(project(":sdl4k"))
	api(project(":sdl2-binaries"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}


application {
	mainClass.set("tetris.MainKt")
	applicationDefaultJvmArgs += "-XstartOnFirstThread"
	tasks.run.get().workingDir = project.projectDir.resolve("src").resolve("main").resolve("resources")
}