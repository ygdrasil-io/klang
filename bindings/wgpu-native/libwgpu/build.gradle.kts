import io.ygdrasil.ParsingMethod
import klang.domain.FunctionPointerType
import klang.domain.ResolvedTypeRef
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
	kotlin("jvm")
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

val headerUrl = URL("https://github.com/gfx-rs/wgpu-native/releases/download/${libs.versions.wgpu.get()}/wgpu-macos-x86_64-release.zip")

klang {

	parsingMethod = ParsingMethod.Libclang

	download(headerUrl)
		.let(::unpack)
		.let {
			parse(fileToParse = "wgpu.h", at = it) {
				// Hardfixes until Callback are fixed
				(findTypeAliasByName("WGPURequestDeviceCallback") ?: error("WGPURequestAdapterCallback should exist"))
					.let { callback ->
						(((callback.typeRef as? ResolvedTypeRef)?.type as? FunctionPointerType) ?: error("should be resolved"))
							.let { function ->
								val arguments = function.arguments.toMutableList()
								arguments[0] = typeOf("int").unchecked()
								arguments[2] = typeOf("char *").unchecked()
								arguments[3] = typeOf("void *").unchecked()
								function.arguments = arguments.toList()
							}
					}
				(findTypeAliasByName("WGPURequestAdapterCallback") ?: error("WGPURequestAdapterCallback should exist"))
					.let { callback ->
						(((callback.typeRef as? ResolvedTypeRef)?.type as? FunctionPointerType) ?: error("should be resolved"))
							.let { function ->
								val arguments = function.arguments.toMutableList()
								arguments[0] = typeOf("int").unchecked()
								arguments[2] = typeOf("char *").unchecked()
								arguments[3] = typeOf("void *").unchecked()
								function.arguments = arguments.toList()
							}
					}
			}
		}

	generateBinding("libwgpu", "WGPU")
}
