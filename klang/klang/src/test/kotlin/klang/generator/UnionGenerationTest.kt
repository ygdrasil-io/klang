package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeStructure
import klang.domain.TypeRefField
import klang.mapper.toSpec
import klang.parser.testType

class UnionGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			TypeRefField("first", testType("long")),
			TypeRefField("second", testType("int")),
			TypeRefField("third", testType("float")),
			TypeRefField("fourth", testType("double")),
			TypeRefField("fifth", testType("void *")),
			TypeRefField("string", testType("char *"))
		),
		isUnion = true,
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		resolveTypes()
	}

	"generate kotlin union" {
		structure.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
public open class MyStructure : com.sun.jna.Union {
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

  override fun read() {
    test.MyStructureDelegate.read(this)
    super.read()
  }

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

