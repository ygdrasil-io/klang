@file:OptIn(ExperimentalSerializationApi::class)

package klang

import klang.parser.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.io.FileInputStream

private val logger = KotlinLogging.logger {}
private val errors = mutableListOf<java.lang.RuntimeException>()

fun main() {
	parseAstJson("jclang/v16/include/dump.json")

	logger.info { "parsing ended with ${errors.size} errors" }
	errors.forEach { logger.error { it } }
	//translationUnitKinds.generateEnum("TranslationUnitKind")
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
		val (kind, _) = node.content

		logger.info { "${(0..depth).map { "+" }}$kind" }

		when (kind) {
			TranslationUnitKind.EnumDecl -> {
				try {
					when {
						node.isTypeDefEnumeration(this) -> {
							index++
							node.toNativeTypeDefEnumeration(this[index])
						}

						else -> {
							node.toNativeEnumeration()
						}
					}
						.also { logger.info { "enum added: $it" } }
						.let(DeclarationRepository::save)
				} catch (e: RuntimeException) {
					errors.add(e)
				}
			}

			else -> {
				//logger.error { "not yet supported: $kind $root" }
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