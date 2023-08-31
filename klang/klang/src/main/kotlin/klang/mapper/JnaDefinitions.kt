package klang.mapper

import com.squareup.kotlinpoet.ClassName

internal val jnaPointer by lazy { ClassName("com.sun.jna", "Pointer") }
internal val jnaCallback by lazy { ClassName("com.sun.jna", "Callback") }
internal val jnaStructure by lazy { ClassName("com.sun.jna", "Structure") }
internal val jnaFieldOrder by lazy { ClassName("com.sun.jna", "Structure.FieldOrder") }
internal val jnaJvmField by lazy { ClassName("kotlin.jvm", "JvmField") }
internal val jnaByReference by lazy { ClassName("com.sun.jna", "Structure.ByReference") }
internal val jnaByValue by lazy { ClassName("com.sun.jna", "Structure.ByValue") }
internal val nsobjectDefinition by lazy { ClassName("darwin", "NSObject") }
internal val jnaNativeLoad by lazy { ClassName("klang.internal", "NativeLoad") }
internal val jnaLibrary by lazy { ClassName("com.sun.jna", "Library") }
internal val jnaPointerType by lazy { ClassName("com.sun.jna", "PointerType") }
internal val jnaPointerByReference by lazy { ClassName("com.sun.jna.ptr", "PointerByReference") }



