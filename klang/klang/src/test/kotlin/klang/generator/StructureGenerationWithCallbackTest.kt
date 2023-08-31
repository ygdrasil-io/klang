package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import klang.parser.testType

class StructureGenerationWithCallbackTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"callback" to testType("void (*)(void *, Uint8 *, int)"),
			"callback2" to testType("MyAlias"),
		)
	)

	val typeAlias = NativeTypeAlias(
		name = "MyAlias",
		type = testType("void (*)(void *, Uint8 *, int)")
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		save(typeAlias)
		resolveTypes()
	}

	"generate kotlin structure with callback" {
		structure.toSpec("test").toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("callback", "callback2")
public open class MyStructure(
  pointer: com.sun.jna.Pointer? = null,
) : com.sun.jna.Structure(pointer) {
  /**
   * mapped from void (*)(void *, Uint8 *, int)
   */
  @kotlin.jvm.JvmField
  public var callback: com.sun.jna.Callback? = null

  /**
   * mapped from MyAlias
   */
  @kotlin.jvm.JvmField
  public var callback2: com.sun.jna.Callback? = null

  public class ByReference(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByReference

  public class ByValue(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByValue
}

		""".trimIndent()
	}


	val structureWithNoFields = NativeStructure(
		name = "MyStructure",
		fields = listOf()
	)

	"generate kotlin structure with no fields" {
		structureWithNoFields.toSpec("test").toString() shouldBe """
			|public class MyStructure : com.sun.jna.PointerType {
			|  public constructor() : super()
			|
			|  public constructor(pointer: com.sun.jna.Pointer?) : super(pointer)
			|
			|  public class ByReference : com.sun.jna.ptr.PointerByReference {
			|    public constructor() : super()
			|
			|    public constructor(pointer: com.sun.jna.Pointer?) : super(pointer)
			|  }
			|}
			|
		""".trimMargin()
	}
})

