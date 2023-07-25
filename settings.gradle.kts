rootProject.name = "klang"

include("docker-toolkit", "klang", "libclang", "toolkit")
include("libclang-generator")
include("binding:darwin:foundation")
findProject(":binding:darwin:foundation")?.name = "foundation"
