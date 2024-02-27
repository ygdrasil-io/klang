import org.apache.tools.ant.taskdefs.condition.Os

plugins {
	`cpp-library`
	`maven-publish`
}

group = "io.ygdrasil"
version = "1.0.0-SNAPSHOT"

library {

	linkage = listOf(Linkage.SHARED)

	targetMachines.add(machines.windows.x86_64)
	targetMachines.add(machines.linux.architecture("aarch64"))
	targetMachines.add(machines.linux.x86_64)
	targetMachines.add(machines.macOS.x86_64)
	targetMachines.add(machines.macOS.architecture("aarch64"))
}

tasks.withType<CppCompile>().configureEach {

	if (Os.isFamily(Os.FAMILY_MAC)) {
		compilerArgs.addAll("-x", "objective-c++")
	}
}

tasks.withType<LinkSharedLibrary>().configureEach {
	linkerArgs.add("-Wl,-undefined,dynamic_lookup")
}
