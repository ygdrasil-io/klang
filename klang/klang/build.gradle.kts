import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.*

val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
val cSourceDir = "$projectDir/src/test/c/"

val sdl2HeadersDir = "$projectDir/src/test/resources/"
val sdl2HeadersFile = file("${sdl2HeadersDir}SDL2-headers-${inferPlatformSuffix()}.zip")
val sdl2HeadersTargetDirectory = "$cSourceDir/SDL2"

val cHeadersDir = "$projectDir/src/main/resources/"
val cHeadersFile = file("${cHeadersDir}c-headers.zip")
val cHeadersTargetDirectory = "$cSourceDir/c"

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

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	implementation("io.github.microutils:kotlin-logging:1.7.4")
	implementation("org.slf4j:slf4j-simple:1.7.26")
	api(project(":libclang"))
	api(project(":jextract"))
	implementation(libs.arrow.core)
	implementation(libs.arrow.fx.coroutines)
	api(libs.kotlinpoet)
	testImplementation(libs.kotest)
}

val unzipSDL2 = task<Copy>("unzipSDL2") {
	val zipTree = zipTree(sdl2HeadersFile)
	onlyIf { !File(sdl2HeadersTargetDirectory).exists() }
	from(zipTree)
	into(cSourceDir)
}

val unzipCHeaders = task<Copy>("unzipCHeaders") {
	val zipTree = zipTree(file(cHeadersFile))
	onlyIf { !File(cHeadersTargetDirectory).exists() }
	from(zipTree)
	into(cSourceDir)
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("--enable-preview")
	dependsOn(unzipSDL2)
	dependsOn(unzipCHeaders)
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		"--enable-preview",
		"--enable-native-access=ALL-UNNAMED"
	)
}

tasks.clean {
	delete(sdl2HeadersTargetDirectory)
	delete(cHeadersTargetDirectory)
}

fun inferPlatformSuffix() = when {
	osName.contains("mac") -> "darwin"
	osName.contains("linux") -> "linux"
	else -> error("OS $osName not supported")
}