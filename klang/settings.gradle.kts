rootProject.name = "klang-toolkit"

include("docker-toolkit", "klang", "libclang", "toolkit-old")
include("libclang-generator")
include("gradle-plugin")
findProject(":gradle-plugin")?.name = "klang-gradle-plugin"
