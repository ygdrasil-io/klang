rootProject.name = "klang-toolkit"

include("docker-toolkit", "klang", "libclang", "toolkit-old")
include("gradle-plugin")
include("jextract")
findProject(":gradle-plugin")?.name = "klang-gradle-plugin"
