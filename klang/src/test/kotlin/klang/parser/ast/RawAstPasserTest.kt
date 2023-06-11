package klang.parser.ast

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class RawAstPasserTest : StringSpec({

	val hierarchyByRawAstUnit = listOf(
		"|-Test" to 1,
		"|  -Test" to 2,
		"|    -Test" to 3,
	)

	hierarchyByRawAstUnit.forEach { (rawAstUnit, expectedHierarchy) ->
		"test hierarchy position of $rawAstUnit" {
			rawAstUnit.getPositionOnHierarchy() shouldBe expectedHierarchy
		}
	}

	val fakeAst = """
		FakeAst
		|-TestLevel1
		| |-TestLevel2
		| `-TestLevel2
		|   `-TestLevel3
		`-TestLevel1
		  `-TestLevel2
		    `-TestLevel3
		""".trimIndent()

	"test raw ast parsing" {
		// Given

		val fakeAstFile = File
			.createTempFile("fakeAst", ".ast")
			.also(File::deleteOnExit)
			.also { it.writeText(fakeAst) }

		// When
		val ast = rawAst(fakeAstFile)

		// Then
		ast.isSuccess shouldBe true
		ast.onSuccess {
			it.size shouldBe 2
			it[0].content shouldBe "TestLevel1"
			it[0].children[0].children.size shouldBe 0
			it[0].children[0].content shouldBe "TestLevel2"
			it[0].children[1].children.size shouldBe 1
			it[0].children[1].content shouldBe "TestLevel2"
			it[0].children[1].children[0].children.size shouldBe 0
			it[0].children[1].children[0].content shouldBe "TestLevel3"
			it[1].content shouldBe "TestLevel1"
			it[1].children[0].children.size shouldBe 1
			it[1].children[0].content shouldBe "TestLevel2"
			it[1].children[0].children[0].children.size shouldBe 0
			it[1].children[0].children[0].content shouldBe "TestLevel3"
		}

	}

})