
plugins {
	kotlin("jvm") version libs.versions.kotlin
	application
	id("org.beryx.jlink") version "3.0.1"
}

version = "1.0.0"

dependencies {
	api(project(":sdl4k"))
	api(project(":sdl2-binaries"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}

application {
	mainModule = "io.ygdrasil"
	mainClass.set("io.ygdrasil.snake.MainKt")
	applicationDefaultJvmArgs += "-XstartOnFirstThread"
	tasks.run.get().workingDir = project.projectDir.resolve("src").resolve("main").resolve("resources")
}

jlink {
	//options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
	launcher{
		moduleName = "io.ygdrasil"
		//name = "Snake"
		jvmArgs = listOf("-XstartOnFirstThread")
	}
}

tasks.named("compileJava", JavaCompile::class.java) {
	options.compilerArgumentProviders.add(CommandLineArgumentProvider {
		// Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
		listOf("--patch-module", "io.ygdrasil=${sourceSets["main"].output.asPath}")
	})
}