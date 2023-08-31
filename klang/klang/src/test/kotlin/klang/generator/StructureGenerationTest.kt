package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.mapper.toSpec
import klang.parser.testType

class StructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"first" to testType("long"),
			"second" to testType("int"),
			"third" to testType("float"),
			"fourth" to testType("double"),
			"fifth" to testType("void *"),
			"enumeration" to testType("MyEnumeration"),
		)
	)

	val enumeration = NativeEnumeration(
		name = "MyEnumeration",
		values = listOf("first" to 1L)
	)

	InMemoryDeclarationRepository().also { repository ->
		repository.save(structure)
		repository.save(enumeration)
		repository.resolveTypes()
	}

	"generate kotlin structure" {
		structure.toSpec("test").toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("first", "second", "third", "fourth", "fifth", "enumeration")
public open class MyStructure(
  pointer: com.sun.jna.Pointer? = null,
) : com.sun.jna.Structure(pointer) {
  /**
   * mapped from long
   */
  @kotlin.jvm.JvmField
  public var first: com.sun.jna.NativeLong = com.sun.jna.NativeLong(0)

  /**
   * mapped from int
   */
  @kotlin.jvm.JvmField
  public var second: kotlin.Int = 0

  /**
   * mapped from float
   */
  @kotlin.jvm.JvmField
  public var third: kotlin.Float = 0.0f

  /**
   * mapped from double
   */
  @kotlin.jvm.JvmField
  public var fourth: kotlin.Double = 0.0

  /**
   * mapped from void *
   */
  @kotlin.jvm.JvmField
  public var fifth: com.sun.jna.Pointer? = null

  /**
   * mapped from MyEnumeration
   */
  @kotlin.jvm.JvmField
  public var enumeration: Int = 0

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

