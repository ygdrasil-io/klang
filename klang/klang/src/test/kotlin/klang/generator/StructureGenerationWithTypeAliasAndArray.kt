package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeStructure
import klang.domain.NativeTypeAlias
import klang.domain.NotBlankString
import klang.domain.TypeRefField
import klang.mapper.toSpec
import klang.parser.testType

class StructureGenerationWithTypeAliasAndArray  : FreeSpec({

	val alias = NativeTypeAlias(
		name = NotBlankString("NewType"),
		typeRef = testType("int")
	)

	val structure = NativeStructure(
		name = NotBlankString("MyStructure"),
		fields = listOf(
			TypeRefField("first", testType("NewType")),
			TypeRefField("second", testType("NewType[4]")),
		)
	)

	InMemoryDeclarationRepository().apply {
		save(alias)
		save(structure)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin structure" {
		structure.toSpec("test").apply {
			size shouldBe 1
			first().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("first", "second")
public open class MyStructure : com.sun.jna.Structure {
  /**
   * mapped from NewType
   */
  @kotlin.jvm.JvmField
  public var first: test.NewType = 0

  /**
   * mapped from NewType[4]
   */
  @kotlin.jvm.JvmField
  public var second: test.`NewType${'$'}Array` = `NewType${'$'}Array`(4)

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
})
