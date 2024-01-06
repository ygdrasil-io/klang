import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

	//exclude("klang/parser/libclang/**")
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
	from(zipTree)
	into(cSourceDir)
}

val unzipCHeaders = task<Copy>("unzipCHeaders") {
	val cSourceDir = "$projectDir/src/test/c/"
	val zipTree = zipTree(file("${cSourceDir}c-headers.zip"))
	from(zipTree)
	into(cSourceDir)
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("--enable-preview")
	//dependsOn(unzipSDL2)
	dependsOn(unzipCHeaders)
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		"--enable-preview",
		"--enable-native-access=ALL-UNNAMED"
		//, "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
	)
	/*systemProperties(
		"java.library.path" to inferPlatformClangPath()?.toFile()?.absolutePath
	)*/
}

private fun inferPlatformClangPath(): Path? {
	val os = System.getProperty("os.name")
	logger.info("will try to find libclang on os $os")
	if (os == "Mac OS X") {
		try {
			val pb: ProcessBuilder = ProcessBuilder().command("/usr/bin/xcode-select", "-p")
			val proc = pb.start()
			val str = String(proc.inputStream.readAllBytes())
			val dir = Paths.get(
				str.trim { it <= ' ' },
				"Toolchains",
				"XcodeDefault.xctoolchain",
				"usr",
				"lib"
			)
			if (Files.isDirectory(dir)) {
				return dir
			}
		} catch (ioExp: IOException) {
			logger.error("fail to find libclang path " + ioExp.stackTraceToString())
		}
	} else if (os == "Linux") {
		val pb: ProcessBuilder = ProcessBuilder().command("/usr/bin/find", "/usr", "-name", "libclang.so")
		val proc = pb.start()
		val str = String(proc.inputStream.readAllBytes())
		logger.info("possible paths to libclang $str")
		val dir = Paths.get(str.trim { it <= ' ' }.split("\n").first())
			.parent
		if (Files.isDirectory(dir)) {
			return dir
		}
	} else {
		logger.error("operating system $os not yet supported")
	}
	logger.error("fail to find libclang path")
	return null
}