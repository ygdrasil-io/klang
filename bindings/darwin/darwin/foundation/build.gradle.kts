import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	kotlin("jvm") version libs.versions.kotlin
}

repositories {
	mavenLocal()
	mavenCentral()
}

group = "io.ygdrasil"
version = "0.0.0"

dependencies {
	api ("net.java.dev.jna:jna:5.13.0")
	api("$group:klang:$version")
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

kotlin {
	jvmToolchain(21)

	sourceSets.all {
		languageSettings {
			java {
				sourceCompatibility = JavaVersion.VERSION_21
				targetCompatibility = JavaVersion.VERSION_21
			}
			languageVersion = "2.0"
		}
	}
}
