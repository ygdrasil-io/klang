package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeStructure
import klang.domain.NotBlankString
import klang.domain.TypeRefField
import klang.mapper.toSpec
import klang.parser.testType

class StructureWithStructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = NotBlankString("MyStructure"),
		fields = listOf(
			TypeRefField("structure", testType("struct MyOtherStructure")),
		)
	)

	val otherStructure = NativeStructure(
		name = NotBlankString("MyOtherStructure"),
		fields = listOf(
			TypeRefField("structure", testType("long")),
		)
	)

	InMemoryDeclarationRepository().apply {
		save(otherStructure)
		save(structure)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin structure" {
		structure.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("structure")
public open class MyStructure : com.sun.jna.Structure {
  /**
   * mapped from struct MyOtherStructure
   */
  @kotlin.jvm.JvmField
  public var structure: test.MyOtherStructure = MyOtherStructure()

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
		name = NotBlankString("MyStructure"),
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

