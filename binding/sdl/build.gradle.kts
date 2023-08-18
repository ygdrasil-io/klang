import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
	kotlin("jvm")  version "1.9.0"
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

val headerUrl = "https://github.com/klang-toolkit/SDL-binary/releases/download/2.28.2-Alpha3/headers.zip"

klang {
	download(headerUrl)
		.let { unpack(it) }
		.let { parse(fileToParse = "SDL2/SDL.h", at = it) }

	generateBinding("libsdl")
}
