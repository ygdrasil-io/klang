import io.ygdrasil.ParsingMethod
import klang.domain.typeOf
import klang.domain.unchecked
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.URI
import io.ygdrasil.noMacros

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
	kotlin("jvm") version libs.versions.kotlin
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

val headerUrl = URI("https://github.com/klang-toolkit/ANGLE-binary/releases/download/2-beta/headers.zip")
	.toURL()

klang {

	parsingMethod = ParsingMethod.Libclang

	download(headerUrl)
		.let(::unpack)
		.let {
			parse(fileToParse = "GLES3/gl3.h", at = it, noMacros) {
				findFunctionByName("glShaderSource")?.let { function ->
					function.arguments.first { it.name?.value == "string" }.apply {
						type = typeOf("char *").unchecked()
					}
				}
			}
		}

	generateBinding("libgles", "GLESv2")
}
