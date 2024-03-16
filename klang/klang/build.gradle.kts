import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.*

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
	api(project(":jextract"))
	implementation(libs.arrow.core)
	implementation(libs.arrow.fx.coroutines)
	api(libs.kotlinpoet)
	testImplementation(libs.kotest)
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		"--enable-preview",
		"--enable-native-access=ALL-UNNAMED"
	)
}
