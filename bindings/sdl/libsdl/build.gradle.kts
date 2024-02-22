import io.ygdrasil.ParsingMethod
import klang.domain.TypeRefField
import klang.domain.typeOf
import klang.domain.unchecked
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.URI

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
	api("io.ygdrasil:angle-binaries:chrome-122.0.6261")
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

val headerUrl = URI("https://github.com/klang-toolkit/SDL-binary/releases/download/2.30.0/headers.zip")
	.toURL() ?: error("cannot create header url")

klang {

	parsingMethod = ParsingMethod.Libclang

	download(headerUrl)
		.let(::unpack)
		.let { it ->
			parse(fileToParse = "SDL2/SDL.h", at = it) {
				findTypeAliasByName("Uint8")?.apply {
					// Type is dumped as Int instead of char
					typeRef = typeOf("char").unchecked()
				}

				// Replace SDL_PixelFormat by void * to avoid circular dependency when calculating size of structure
				findStructureByName("SDL_PixelFormat")?.apply {
					fields = fields
						.filterIsInstance<TypeRefField>()
						.map { (name, fields) ->
							when (name) {
								"next" -> name to typeOf("void *").unchecked()
								else -> name to fields
							}
						}.map { TypeRefField(it.first, it.second) }
				}
			}
			parse(fileToParse = "SDL2/SDL_opengles2.h", at = it)
		}

	generateBinding("io.ygdrasil.libsdl", "SDL2")
}

tasks.named("compileJava", JavaCompile::class.java) {
	options.compilerArgumentProviders.add(CommandLineArgumentProvider {
		// Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
		listOf("--patch-module", "io.ygdrasil.libsdl=${sourceSets["main"].output.asPath}")
	})
}