import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
	val cSourceDir = "$projectDir/src/test/c/"
	val zipTree = zipTree(file("${cSourceDir}SDL2-headers.zip"))
	onlyIf { !File("$cSourceDir/SDL2").exists() }
	from(zipTree)
	into(cSourceDir)
}

val unzipCHeaders = task<Copy>("unzipCHeaders") {
	val cSourceDir = "$projectDir/src/test/c/"
	val zipTree = zipTree(file("${cSourceDir}c-headers.zip"))
	onlyIf { !File("$cSourceDir/c").exists() }
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
