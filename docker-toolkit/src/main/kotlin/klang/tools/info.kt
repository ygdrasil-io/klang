package klang.tools

// TODO: not working if docker is not running
fun dockerIsRunning() = "docker".runCommand(listOf("info")).exitCode == 0
