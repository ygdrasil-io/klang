import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import io.ygdrasil.downloadFile
import klang.domain.NativeFunction
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
	kotlin("jvm") version "1.9.0"
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
	resources.srcDir("$buildDir/resources")
}

val headerUrl = "https://github.com/ygdrasil-io/libclang-binary/releases/download/16.0.6-alpha1/headers.zip"
	.let(::URL)

klang {
	download(headerUrl)
		.let(::unpack)
		.let { pathRef ->
			parse(fileToParse = "clang-c/Index.h", at = pathRef) {
				declarations.filterIsInstance<NativeFunction>()
					.filter { it.name == "clang_parseTranslationUnit" }
					.forEach { function ->
						function.arguments
							.filter { it.name == "command_line_args" }
							.forEach { parameter -> parameter.type.isArray = true }
						}
			}
		}

	generateBinding("libclang", "clang")
}

tasks {
	register("downloadClangBinary") {
		doLast {
			downloadFile(
				fileUrl = URL("https://github.com/ygdrasil-io/libclang-binary/releases/download/16.0.6-alpha1/libclang.dylib"),
				targetFile = project.buildDir.resolve("resources").resolve("darwin").resolve("libclang.dylib")
			)
		}
	}
}