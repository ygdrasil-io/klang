import klang.domain.typeOf
import klang.domain.unchecked
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.URL

buildscript {
	dependencies {
		classpath("io.ygdrasil:klang:1.0.0-SNAPSHOT") {
			isChanging = true
		}
		classpath("io.ygdrasil:klang-gradle-plugin:1.0.0-SNAPSHOT") {
			isChanging = true
		}
	}
}

plugins {
	kotlin("jvm") version "1.9.10"
	alias(libs.plugins.klang)
}

dependencies {
	api(libs.jna)
	testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("io.github.libsdl4j:libsdl4j:2.28.1-1.3")
	testImplementation(libs.kotest)
}

tasks.test {
	useJUnitPlatform()
	maxHeapSize = "4g"
	minHeapSize = "512m"

	testLogging {
		events = setOf(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
		exceptionFormat = TestExceptionFormat.FULL
		showExceptions = true
		showStackTraces = true
		showStandardStreams = true
	}

}

sourceSets.main {
	val buildDir = layout.buildDirectory.locationOnly.get().asFile
		.resolve("generated")
		.resolve("klang")
		.absolutePath
	println("will add \"$buildDir\" to source set")
	java.srcDirs(buildDir)
}

val headerUrl = URL("https://github.com/klang-toolkit/SDL-binary/releases/download/2.28.2-Alpha3/headers.zip")

klang {
	download(headerUrl)
		.let(::unpack)
		.let {
			parse(fileToParse = "SDL2/SDL.h", at = it) {
				findTypeAliasByName("Uint8")?.apply {
					// Type is dumped as Int instead of char
					typeRef = typeOf("char").unchecked()
				}

				// Replace SDL_PixelFormat by void * to avoid circular dependency when calculating size of structure
				findStructureByName("SDL_PixelFormat")?.apply {
					fields = fields.map { (name, fields) ->
						when (name) {
							"next" -> name to typeOf("void *").unchecked()
							else -> name to fields
						}
					}
				}

				// Array must be set manually as there is no way to know it without reading the documentation
				findStructureByName("SDL_AudioCVT")?.apply {
					fields.find { (name, _) -> name == "filters" }
						?.let { (_, field) ->
							field.isArray = true
							field.arraySize = 10
						}
				}
				findStructureByName("SDL_Event")?.apply {
					fields.find { (name, _) -> name == "padding" }
						?.let { (_, field) ->
							field.isArray = true
							field.arraySize = 56
						}
				}
			}
		}

	generateBinding("libsdl", "SDL2")
}
