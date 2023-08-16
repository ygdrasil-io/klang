import io.ygdrasil.KlangPluginExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
	}
	dependencies {
		classpath("io.ygdrasil:klang-gradle-plugin:1.0.0-SNAPSHOT") {
			isChanging = true
		}
	}
}

plugins {
	alias(libs.plugins.klang)
	kotlin("jvm")
}

dependencies {
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

	exclude("klang/parser/libclang/**")
}

sourceSets.main {
	//java.srcDirs("src/main/generated")
}

val headerUrl = "https://github.com/klang-toolkit/SDL-binary/releases/download/2.28.2-Alpha2/headers.zip"

klang {
	/*download(headerUrl)
		.let { unpack(it) }
		.let { parse(header = "SDL.h", at = it) }*/
}

