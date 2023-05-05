package klang.parser.domain.type

import klang.jvm.Type
import klang.parser.domain.declaration.Scoped


abstract class Typed(val isErroneous: Boolean = false) {

    class Error : Typed(true)
    class Unresolved(val type: Type): Typed()
    class Declared(val fullName: String, val reference: Scoped) : Typed()

}
