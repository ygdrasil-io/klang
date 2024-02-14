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
import klang.parser.TestData.basicFunctionPointer
import klang.parser.testType

class StructureGenerationWithCallbackTest : FreeSpec({

	val structure = NativeStructure(
		name = NotBlankString("MyStructure"),
		fields = listOf(
			TypeRefField("callback", testType(basicFunctionPointer)),
			TypeRefField("callback2", testType("MyAlias"))
		)
	)

	val typeAlias = NativeTypeAlias(
		name = NotBlankString("MyAlias"),
		typeRef = testType(basicFunctionPointer)
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		save(typeAlias)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin structure with callback" {
		structure.toSpec("test").apply {
			size shouldBe 2
			let { (callback, structure) ->
				callback.toString() shouldBe """
					|public interface MyStructureCallbackFunction : com.sun.jna.Callback {
					|  public operator fun invoke(
					|    param1: com.sun.jna.Pointer,
					|    param2: kotlin.String,
					|    param3: kotlin.Int,
					|  )
					|}
					|
		""".trimMargin()
				structure.toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("callback", "callback2")
public open class MyStructure : com.sun.jna.Structure {
  /**
   * mapped from void (*)(void *, char *, int)
   */
  @kotlin.jvm.JvmField
  public var callback: test.MyStructureCallbackFunction? = null

  /**
   * mapped from MyAlias
   */
  @kotlin.jvm.JvmField
  public var callback2: test.MyAlias? = null

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

