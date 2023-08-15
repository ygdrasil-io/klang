package klang.tools

fun dockerIsRunning() = "docker".runCommand(listOf("info")).exitCode == 0
