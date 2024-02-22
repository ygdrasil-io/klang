package klang.parser

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.NativeConstant
import klang.domain.NativeStructure
import klang.domain.NotBlankString
import klang.helper.HeaderManager
import klang.parser.json.ParserRepository
import klang.parser.libclang.parseFile
import mu.KotlinLogging
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

@Ignored
open class ParserTestCommon(body: FreeSpec.() -> Unit = {}) : FreeSpec({

	beforeContainer {
		ParserRepository.errors.clear()
	}

	beforeTest {
		ParserRepository.errors.size shouldBe 0
	}

	body()
})

suspend fun File.parseIt(function: suspend DeclarationRepository.() -> Unit) = InMemoryDeclarationRepository().parseFile(absolutePath)
	.function()

fun createHeader(fileName: String, function: () -> String): File {
	val tempDirectory = HeaderManager.createTemporaryHeaderDirectory()
	val headerFile = tempDirectory.resolve(fileName)
	val content = function()
	check(content.isNotBlank()) { "header code should not be blank" }
	Files.write(headerFile, content.toByteArray(StandardCharsets.UTF_8))

	return headerFile.toFile()
}

 suspend fun FreeSpecContainerScope.validateEnumerations(repository: DeclarationRepository, enumerations: List<Pair<NotBlankString, List<Pair<String, Long>>>>) {
	enumerations.forEach { (name, values) ->
		"test $name" {
			repository.findEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}
}

suspend fun FreeSpecContainerScope.validateStructures(repository: DeclarationRepository, structures: List<NativeStructure>) {
	structures.forEach { (name, fields, isUnion) ->
		"test $name" {
			repository.findStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
				.also { it?.isUnion shouldBe isUnion }
		}
	}
}

suspend fun FreeSpecContainerScope.validateConstants(repository: DeclarationRepository, constants: List<NativeConstant<*>>) {
	constants.forEach { (name, value, _) ->
		"expected $name with value $value" {
			repository.findConstantByName(name)
				.also { it?.name shouldBe name }
				.also { it?.value shouldBe value }
		}
	}
}
