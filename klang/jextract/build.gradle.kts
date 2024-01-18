import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

plugins {
	id("de.undercouch.download") version "4.1.2"
}

dependencies {
	implementation("io.github.microutils:kotlin-logging:1.7.4")
	implementation("org.slf4j:slf4j-simple:1.7.26")
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("--enable-preview")
}

task("runTest", JavaExec::class) {
	jvmArgs(
		"--enable-preview",
		"--enable-native-access=ALL-UNNAMED"
	)
	systemProperties("java.library.path" to "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib")
	classpath = sourceSets["main"].runtimeClasspath
	mainClass = "klang.TestKt"
}

val baseUrl = "https://github.com/klang-toolkit/libclang-binary/releases/download/15/"
val fileToDownload = listOf(
	"libclang-arm64.dylib",
	"libclang-x86_64.dylib",
	"libclang-x86_64.so",
).forEach { fileName ->
	val url = "$baseUrl$fileName"
	val taskName = "downloadFile-$fileName"
	tasks.register<Download>(taskName) {
		val directory = project.file("src/main/resources")
		onlyIf { !directory.resolve(fileName).exists() }
		src(url)
		dest(directory)
	}

	tasks.named("processResources") {
		dependsOn(taskName)
	}
}