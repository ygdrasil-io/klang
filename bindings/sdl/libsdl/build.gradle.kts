import arrow.core.right
import klang.domain.typeOf
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
	kotlin("jvm")  version "1.9.10"
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
	java.srcDirs("$buildDir/generated/klang")
}

val headerUrl = URL("https://github.com/klang-toolkit/SDL-binary/releases/download/2.28.2-Alpha3/headers.zip")

klang {
	download(headerUrl)
		.let(::unpack)
		.let {
			parse(fileToParse = "SDL2/SDL.h", at = it) {
				findTypeAliasByName("Uint8")?.apply {
					// Type is dumped as Int instead of char
					typeOf("char").onRight {
						this.type = it
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
			}
		}

	generateBinding("libsdl", "SDL2")
}
