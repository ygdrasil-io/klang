package klang.mapper

import com.squareup.kotlinpoet.ClassName

internal val jnaPointer by lazy { ClassName("com.sun.jna", "Pointer") }
internal val jnaStructure by lazy { ClassName("com.sun.jna", "Structure") }
internal val jnaFieldOrder by lazy { ClassName("com.sun.jna", "Structure.FieldOrder") }