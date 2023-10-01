rootProject.name = "klang-toolkit"

include("docker-toolkit", "klang", "libclang", "toolkit-old")
include("gradle-plugin")
findProject(":gradle-plugin")?.name = "klang-gradle-plugin"
