@file:OptIn(ExperimentalSerializationApi::class)

package klang

import klang.parser.json.ParserRepository
import klang.parser.json.domain.Node
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.toNode
import klang.parser.json.type.isTypeDefEnumeration
import klang.parser.json.type.toNativeEnumeration
import klang.parser.json.type.toNativeStructure
import klang.parser.json.type.toNativeTypeDefEnumeration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.io.FileInputStream

private val logger = KotlinLogging.logger {}

fun main() {
	parseAstJson("jclang/v16/include/dump.json")

	logger.info { "parsing ended with ${ParserRepository.errors.size} errors" }
	ParserRepository.errors.forEach { logger.error { it } }
}

fun parseAstJson(filePath: String) = FileInputStream(filePath)
	.let<FileInputStream, JsonObject>(Json.Default::decodeFromStream)
	.toNode()
	.flattenRootNode()
	.removeImplicitDeclarations()
	.parse()

fun List<TranslationUnitNode>.parse(depth: Int = 0) {
	var index = 0

	while (index != size) {
		val node = this[index]
		val (kind, json) = node.content


		when (kind) {
			TranslationUnitKind.RecordDecl -> {
				node.toNativeStructure()
					.let(DeclarationRepository::save)
			}

			TranslationUnitKind.EnumDecl -> {
				try {
					when {
						node.isTypeDefEnumeration(this) -> {
							index++
							node.toNativeTypeDefEnumeration(this[index])
						}

						else -> node.toNativeEnumeration()
					}.let(DeclarationRepository::save)
				} catch (e: RuntimeException) {
					ParserRepository.errors.add(e)
				}
			}

			else -> {
				logger.info { "${(0..depth).map { "+" }}$kind${json["id"]}" }
			}
		}

		index++

	}

}

private fun <T> Node<T>.flattenRootNode() = children

private fun List<TranslationUnitNode>.removeImplicitDeclarations(): List<TranslationUnitNode> =
	mapNotNull { it.removeImplicitDeclarations() }

private fun TranslationUnitNode.removeImplicitDeclarations() = content.let { (_, node) ->
	when (node["isImplicit"]?.jsonPrimitive?.booleanOrNull ?: false) {
		false -> this
		else -> null
	}
}