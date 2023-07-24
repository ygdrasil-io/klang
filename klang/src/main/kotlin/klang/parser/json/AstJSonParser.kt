@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package klang.parser.json

import klang.DeclarationRepository
import klang.domain.NativeDeclaration
import klang.parser.json.domain.Node
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.toNode
import klang.parser.json.type.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.io.FileInputStream

private val logger = KotlinLogging.logger {}

fun parseAstJson(filePath: String) = FileInputStream(filePath)
	.let<FileInputStream, JsonObject>(Json.Default::decodeFromStream)
	.toNode()
	.flattenRootNode()
	.removeImplicitDeclarations()
	.parse()


fun List<TranslationUnitNode>.parse(depth: Int = 0) {
	logger.info { "start processing nodes" }
	var index = 0

	while (index != size) {
		val node = this[index]
		val (kind, json) = node.content


		try {
			logger.info { "will process node of kind $kind" }
			when (kind) {
				TranslationUnitKind.ObjCInterfaceDecl -> node.toObjectiveCClass()
				TranslationUnitKind.TypedefDecl -> node.toNativeTypeAlias()
				TranslationUnitKind.VarDecl -> {
					if (node.isExternalDeclaration().not()) {
						error("not yet supported : $node")
					}
					null
				}

				TranslationUnitKind.FunctionDecl -> node.toNativeFunction()
				TranslationUnitKind.RecordDecl -> when {
					node.isTypeDefStructure(this) -> {
						index++
						node.toNativeTypeDefStructure(this[index])
					}

					else -> node.toNativeStructure()
				}

				TranslationUnitKind.EnumDecl -> when {
					node.isTypeDefEnumeration(this) -> {
						index++
						node.toNativeTypeDefEnumeration(this[index])
					}

					else -> node.toNativeEnumeration()
				}

				else -> {
					logger.info { "${(0..depth).map { "+" }}$kind${json["id"]}" }
					null
				}
			}.takeIf { it is NativeDeclaration }
				?.let(DeclarationRepository::save)
		} catch (e: RuntimeException) {
			logger.error { "fail with error $e" }
			ParserRepository.errors.add(e)
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