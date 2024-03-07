import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.compose)
}

repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
	maven {
		url = uri("http://repo.maven.cyberduck.io.s3.amazonaws.com/releases")
		isAllowInsecureProtocol = true
	}
}

kotlin {
	jvm()

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(":examples:common"))
			}
		}

		val jvmMain by getting {
			//configurations["jvmMainCompileOnly"].isCanBeResolved = true
			dependencies {
				implementation(compose.desktop.currentOs)
				implementation("net.java.dev.jna:jna:5.14.0")
				implementation("net.java.dev.jna:jna-platform:5.14.0")
				implementation("org.rococoa:rococoa-core:0.9.1")
				implementation("org.rococoa:librococoa:0.9.1")
				implementation("org.apache.logging.log4j:log4j-api:2.20.0")
				implementation("org.apache.logging.log4j:log4j-core:2.20.0")
				implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

			}
			/*
			tasks.register<Copy>("copyFileFromDependency") {
				val dependencyFile = configurations.runtimeClasspath.get()
					.filter { it.name.contains("librococoa-0.9.1.dylib") }
					.singleFile
				from(dependencyFile)
				into("src/main/resources/darwin/")
				rename { "librococoa.dylib" }
			}
			*/
		}
	}
}

compose.desktop {
	application {
		mainClass = "MainKt"

		jvmArgs += "--add-opens=java.base/java.lang=ALL-UNNAMED"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "compose"
			packageVersion = "1.0.0"
		}
	}
}


/*
configurations
	.filter { it.name.contains("jvmMainCompileOnly") }
	.first()
	.let { it.asPath }
	.let { zipTree(it) }
	.forEach { println(it) }


val smokeTest by configurations.creating {

}

dependencies {
	smokeTest("org.rococoa:librococoa:0.9.1")
}
smokeTest
	.forEach { println(it) }
	//.let(::zipTree)
	//.files
	//.filter { it.name.contains("librococoa-0.9.1.dylib") }
	//.let { println(it.) }

smokeTest.forEach { println(it.name) }*/

/*tasks.register<Copy>("copyFileFromDependency") {
	val runtimeClasspath = configurations["kotlinJvmRuntimeClasspath"]
	val dependencyFile = runtimeClasspath.get()
		.filter { it.name.contains("librococoa-0.9.1.dylib") }
		.singleFile
	from(dependencyFile)
	into("src/jvmMain/resources/darwin/")
	rename { "librococoa.dylib" }
}*/
