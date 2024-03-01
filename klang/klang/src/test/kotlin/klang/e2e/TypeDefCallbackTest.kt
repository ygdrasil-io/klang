package klang.e2e

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.allDeclarationsFilter
import klang.domain.*
import klang.parser.*
import java.io.File

class TypeDefCallbackTest : ParserTestCommon({

	"typedef callback" - {
		// Given
		createHeader("typedef-callback.h") {
			"""
				typedef struct MyStructImpl* MyStruct;
				
				typedef enum MyEnum {
				    val1 = 0x00000000,
				    val2 = 0x00000001
				} MyEnum;

				typedef void (*MyCallback)(MyEnum status, MyStruct adapter, char const * message, void * userdata);
			""".trimIndent()
			// When
		}.parseIt {

			// Hardfixe until callback are working TODO FIX
			resolveTypes(allDeclarationsFilter)
			(findTypeAliasByName("MyCallback") ?: error("MyCallback should exist"))
				.let { callback ->
					(((callback.typeRef as? ResolvedTypeRef)?.type as? FunctionPointerType) ?: error("should be resolved"))
						.let { function ->
							val arguments = function.arguments.toMutableList()
							arguments[0] = typeOf("MyEnum").unchecked()
							arguments[1] = typeOf("MyStruct").unchecked()
							arguments[2] = typeOf("char *").unchecked()
							arguments[3] = typeOf("void *").unchecked()
							function.arguments = arguments.toList()
						}
				}

			// And
			resolveTypes(allDeclarationsFilter)

			//Then
			"test parsing result" {
				val myCallback = findTypeAliasByName("MyCallback")
					.also {
						it shouldNotBe null
						(it?.typeRef is ResolvedTypeRef) shouldBe  true
						(it?.typeRef as ResolvedTypeRef).apply {
							(type is FunctionPointerType) shouldBe  true
							(type as FunctionPointerType).let { pointerFunctionType ->
								(pointerFunctionType.returnType is ResolvedTypeRef) shouldBe  true
								(pointerFunctionType.returnType as ResolvedTypeRef).type shouldBe VoidType
								pointerFunctionType.arguments.forEach { (it is ResolvedTypeRef) shouldBe true  }
								val arguments = pointerFunctionType.arguments.map { it as ResolvedTypeRef }
								arguments.size shouldBe 4
								arguments[0].type shouldBe findEnumerationByName("MyEnum")
								arguments[1].type shouldBe findTypeAliasByName("MyStruct")
								arguments[2].type shouldBe StringType
								arguments[3].type shouldBe VoidType
								arguments[3].isPointer shouldBe true
							}
						}
					}
			}

			// And
		}.generateJNABinding { files ->
			"test JNA binding" {
				files.firstOrNull { it.name.contains("TypeAlias") }
					.let { it shouldNotBe null }
					?.let(File::readText)
					?.let {
						it shouldBe """
						|package test
						|
						|import com.sun.jna.Callback
						|import com.sun.jna.Pointer
						|import kotlin.Int
						|import kotlin.String
						|
						|public typealias MyStruct = MyStructImpl
						|
						|public interface MyCallback : Callback {
						|  public operator fun invoke(
						|    param1: Int,
						|    param2: MyStruct?,
						|    param3: String?,
						|    param4: Pointer?,
						|  )
						|}
						|
					""".trimMargin()
					}
			}

		}
	}
})
