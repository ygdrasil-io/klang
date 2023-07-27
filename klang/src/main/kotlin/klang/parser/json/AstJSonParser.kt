@file:OptIn(
	ExperimentalSerializationApi::class
)

package klang.parser.json

import klang.DeclarationRepository
import klang.domain.NativeDeclaration
import klang.parser.json.domain.*
import klang.parser.json.type.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.io.FileInputStream

private val logger = KotlinLogging.logger {}

val unknownKind = mutableSetOf<String>()

fun parseAstJson(filePath: String) = FileInputStream(filePath)
	.let<FileInputStream, JsonObject>(Json.Default::decodeFromStream)
	.validateKinds()
	.also { unknownKind.forEach { println("$it,") } }
	.toNode()
	.flattenRootNode()
	.removeImplicitDeclarations()
	.parse()

private fun JsonObject.validateKinds(): JsonObject = this.apply {
	try {
		kind()
	} catch (e: RuntimeException) {
		this["kind"]
			?.let(JsonElement::jsonPrimitive)
			?.let(JsonPrimitive::content)
			?.let(unknownKind::add)
	}
	this["inner"]
		?.jsonArray
		?.mapNotNull { (it as? JsonObject) }
		?.map { it.validateKinds() }
}


fun List<TranslationUnitNode>.parse(depth: Int = 0) {
	logger.debug { "start processing nodes" }
	var index = 0

	while (index != size) {
		val node = this[index]
		val (kind, json) = node.content


		try {
			logger.debug { "will process node of kind $kind" }
			when (kind) {
				TranslationUnitKind.ObjCInterfaceDecl -> node.toObjectiveCClass()
				TranslationUnitKind.ObjCProtocolDecl -> node.toObjectiveCProtocol()
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
		} catch (error: RuntimeException) {
			//logger.error(error) { "fail to parse node at $json" }
			ParserRepository.errors.add(error)
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