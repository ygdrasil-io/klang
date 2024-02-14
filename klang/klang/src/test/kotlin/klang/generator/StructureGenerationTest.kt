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

class StructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = NotBlankString("MyStructure"),
		fields = listOf(
			TypeRefField("first", testType("long")),
			TypeRefField("second", testType("int")),
			TypeRefField("third", testType("float")),
			TypeRefField("fourth", testType("double")),
			TypeRefField("fifth", testType("void *")),
			TypeRefField("string", testType("char *"))
		)
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin structure" {
		structure.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("first", "second", "third", "fourth", "fifth", "string")
public open class MyStructure : com.sun.jna.Structure {
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
   * mapped from char *
   */
  @kotlin.jvm.JvmField
  public var string: kotlin.String = ""

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

