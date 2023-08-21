rootProject.name = "klang"

include("docker-toolkit", "klang", "libclang", "toolkit")
include("libclang-generator")
include("gradle-plugin")
findProject(":gradle-plugin")?.name = "klang-gradle-plugin"
