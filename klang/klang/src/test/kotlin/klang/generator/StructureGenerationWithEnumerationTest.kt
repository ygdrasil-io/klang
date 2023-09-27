package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.mapper.toSpec
import klang.parser.testType

class StructureGenerationWithEnumerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"enumeration" to testType("MyEnumeration"),
		)
	)

	val enumeration = NativeEnumeration(
		name = "MyEnumeration",
		values = listOf("first" to 1L)
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		save(enumeration)
		resolveTypes()
	}

	"generate kotlin structure with enumeration" {
		structure.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("enumeration")
public open class MyStructure : com.sun.jna.Structure {
  /**
   * mapped from MyEnumeration
   */
  @kotlin.jvm.JvmField
  public var enumeration: kotlin.Int = 0

  public constructor(pointer: com.sun.jna.Pointer?) : super(pointer)

  public constructor()

  public class ByReference(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByReference

  public class ByValue(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByValue
}

		""".trimIndent()
		}
	}


	val structureWithNoFields = NativeStructure(
		name = "MyStructure",
		fields = listOf()
	)

	"generate kotlin structure with no fields" {
		structureWithNoFields.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
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
	}
})

