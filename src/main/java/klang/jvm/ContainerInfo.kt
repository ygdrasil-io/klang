package klang.jvm

import klang.jvm.binding.CXIdxContainerInfo

class ContainerInfo(info: CXIdxContainerInfo) {
    val cursor = Cursor(info.cursor)

}