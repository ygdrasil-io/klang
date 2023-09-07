package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeStructure
import klang.mapper.toSpec
import klang.parser.testType

class StructureWithStructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"structure" to testType("struct MyOtherStructure"),
		)
	)

	val otherStructure = NativeStructure(
		name = "MyOtherStructure",
		fields = listOf(
			"structure" to testType("long"),
		)
	)

	InMemoryDeclarationRepository().apply {
		save(otherStructure)
		save(structure)
		resolveTypes()
	}

	"generate kotlin structure" {
		structure.toSpec("test").toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("structure")
public open class MyStructure(
  pointer: com.sun.jna.Pointer? = null,
) : com.sun.jna.Structure(pointer) {
  /**
   * mapped from struct MyOtherStructure
   */
  @kotlin.jvm.JvmField
  public var structure: test.MyOtherStructure = MyOtherStructure()

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

