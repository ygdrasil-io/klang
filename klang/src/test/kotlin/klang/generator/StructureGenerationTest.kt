package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.KotlinEnumeration
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.mapper.toSpec

class StructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"first" to "Long",
			"second" to "Int",
		)

	)

	"generate kotlin structure" {
		structure.toSpec().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("first", "second")
public open class MyStructure(
  pointer: com.sun.jna.Pointer? = null
) : com.sun.jna.Structure(pointer) {
  @JvmField var first: Long = 0
  @JvmField var second: Int = 0


    class ByReference(pointer: Pointer? = null) : MyStructure(pointer), ByReference
    class ByValue(pointer: Pointer? = null) : MyStructure(pointer), ByValue
}

		""".trimIndent()
	}
})


/*@Structure.FieldOrder("scancode", "sym", "mod", "unused")
open class SDL_Keysym(pointer: Pointer? = null) : Structure(pointer) {
    @JvmField var scancode: Int = 0
    @JvmField var sym: SDL_Keycode = 0
    @JvmField var mod: Uint16 = 0
    @JvmField var unused: Uint32 = 0

    class Ref(pointer: Pointer? = null) : SDL_Keysym(pointer), ByReference
}*/