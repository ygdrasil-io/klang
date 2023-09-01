package klang.parser.libclang

import arrow.core.getOrElse
import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.*
import klang.jvm.AbstractIndexerCallback
import klang.jvm.CursorKind
import klang.jvm.DeclarationInfo
import klang.jvm.createIndex
import klang.parser.libclang.type.declareFunction
import klang.parser.tools.OneTimeProvider
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal data class ParsingContext(
	var currentDefinition: NativeDeclaration? = null,
	var lastTypeDefName: OneTimeProvider<String> = OneTimeProvider(),
	val declarationRepository: DeclarationRepository = InMemoryDeclarationRepository()
) {
	inline fun <reified T : NativeDeclaration> getCurrentDefinitionAs(): T {
		return (currentDefinition as? T)
			?: throw IllegalStateException("Expected ${T::class.simpleName}")
	}
}

fun parseFile(file: String) =
	ParsingContext()
		.parse(file) { info: DeclarationInfo ->
			logger.debug { "parsing unit at ${info.location}" }
			when (info.cursor.kind) {
				CursorKind.TYPEDEF_DECL -> when {
					isEnumOrStruct(info) -> storeSpelling(info)
					else -> declareTypeAlias(info)
				}

				CursorKind.ENUM_DECL -> declareEnumeration(info)
				CursorKind.STRUCT_DECL -> declareStructure(info)
				CursorKind.ENUM_CONSTANT_DECL -> updateEnumerationField(info)
				CursorKind.FIELD_DECL -> updateStructureField(info)
				CursorKind.FUNCTION_DECL -> declareFunction(info)

				else -> println("not found ${info.cursor.kind} ${info.cursor.spelling}")
			}

		}


private fun ParsingContext.parse(file: String, block: ParsingContext.(DeclarationInfo) -> Unit) =
	createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false)
		.use { index ->
			index.indexSourceFile(object : AbstractIndexerCallback() {
				override fun indexDeclaration(info: DeclarationInfo) {
					block(info)
				}
			}, file)
		}.let { declarationRepository }

private fun isEnumOrStruct(info: DeclarationInfo) = info.cursor.children().isNotEmpty()
	&& info.cursor.children().first().kind in listOf(CursorKind.ENUM_DECL, CursorKind.STRUCT_DECL)

private fun ParsingContext.declareTypeAlias(info: DeclarationInfo) {
	val name = info.cursor.spelling
	val type = info.cursor.underlyingType.spelling
	currentDefinition = NativeTypeAlias(
		name = name,
		type = type.let(::typeOf).unchecked("fail to parse type $this")
	).also(declarationRepository::save)
}

private fun ParsingContext.updateStructureField(info: DeclarationInfo) {
	val name = info.cursor.spelling
	val value = typeOf(info.cursor.type.spelling).unchecked("fail to parse type $this")
	currentDefinition = getCurrentDefinitionAs<NativeStructure>().let {
		declarationRepository.update(it) {
			it.copy(fields = it.fields + (name to value))
		}
	}
}

private fun ParsingContext.updateEnumerationField(info: DeclarationInfo) {
	val name = info.cursor.spelling
	val value = info.cursor.getEnumConstantValue()
	currentDefinition = getCurrentDefinitionAs<NativeEnumeration>().let {
		declarationRepository.update(it) {
			it.copy(values = it.values + (name to value))
		}
	}
}

private fun ParsingContext.declareStructure(info: DeclarationInfo) {
	currentDefinition = NativeStructure(lastTypeDefName.consume() ?: info.cursor.spelling)
		.also(declarationRepository::save)
}

private fun ParsingContext.declareEnumeration(info: DeclarationInfo) {
	currentDefinition = NativeEnumeration(lastTypeDefName.consume() ?: info.cursor.spelling)
		.also(declarationRepository::save)
}

private fun ParsingContext.storeSpelling(info: DeclarationInfo) {
	lastTypeDefName.store(info.cursor.spelling)
}