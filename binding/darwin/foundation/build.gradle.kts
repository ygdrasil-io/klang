import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	kotlin("jvm")
}

dependencies {
	api ("net.java.dev.jna:jna:5.13.0")
	implementation(project(":klang"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.1")
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
	java.srcDirs("src/main/generated")
}
