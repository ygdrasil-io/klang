import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

plugins {
	kotlin("jvm") version libs.versions.kotlin
	`maven-publish`
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") { from(components["java"]) }
	}
}
version = "chrome-122.0.6261"

val directory = project.file("src/main/resources")
val baseUrl = "https://github.com/klang-toolkit/ANGLE-binary/releases/download/$version/"
val fileToDownload = listOf(
	"libEGL.dylib" to directory.resolve("darwin").resolve("libEGL.dylib"),
	"libGLESv2.dylib" to directory.resolve("darwin").resolve("libGLESv2.dylib"),
	"libEGL.dll" to directory.resolve("win32").resolve("libEGL.dylib"),
	"libGLESv2.dll" to directory.resolve("win32").resolve("libGLESv2.dylib"),
).forEach { (fileName, target) ->
	val url = "$baseUrl$fileName"
	val taskName = "downloadFile-$fileName"
	tasks.register<Download>(taskName) {
		onlyIf { !target.exists() }
		src(url)
		dest(target)
	}

	tasks.named("processResources") {
		dependsOn(taskName)
	}
}