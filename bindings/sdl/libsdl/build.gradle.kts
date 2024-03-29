import klang.domain.typeOf
import klang.domain.unchecked
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.URL

buildscript {
	dependencies {
		classpath("io.ygdrasil:klang:0.0.0") {
			isChanging = true
		}
		classpath("io.ygdrasil:klang-gradle-plugin:0.0.0") {
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
			}
		}

	generateBinding("libsdl", "SDL2")
}
