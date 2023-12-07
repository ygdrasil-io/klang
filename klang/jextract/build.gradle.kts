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