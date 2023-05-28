package klang.parser.libclang

import klang.DeclarationRepository
import klang.domain.NativeDeclaration
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.jvm.AbstractIndexerCallback
import klang.jvm.CursorKind
import klang.jvm.DeclarationInfo
import klang.jvm.createIndex
import klang.parser.tools.OneTimeProvider

private data class ParsingContext(
	var currentDefinition: NativeDeclaration? = null,
	var lastTypeDefName: OneTimeProvider<String> = OneTimeProvider()
) {
	inline fun <reified T : NativeDeclaration> getCurrentDefinitionAs(): T {
		return (currentDefinition as? T)
			?: throw IllegalStateException("Expected ${T::class.simpleName}")
	}
}

fun parseFile(file: String) {
	ParsingContext().apply {
		val index = createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false)
		index.indexSourceFile(object : AbstractIndexerCallback() {
			override fun indexDeclaration(info: DeclarationInfo) {
				when (info.cursor.kind) {
					CursorKind.TYPEDEF_DECL -> storeSpelling(info)
					CursorKind.ENUM_DECL -> declareEnumeration(info)
					CursorKind.STRUCT_DECL -> declareStructure(info)
					CursorKind.ENUM_CONSTANT_DECL -> updateEnumerationField(info)
					CursorKind.FIELD_DECL -> updateStructureField(info)

					else -> println("not found ${info.cursor.kind} ${info.cursor.spelling}")
				}

				for (attribute in info.attributes) {
					val location = attribute.location
					print("  " + location.getLine() + ":" + location.getColumn())
					print(" " + attribute.kind)
					print(" " + attribute.cursor.kind)
					println()
				}
			}


		}, file)
	}
}

private fun ParsingContext.updateStructureField(info: DeclarationInfo) {
	val name = info.cursor.spelling
	val value = info.cursor.type.spelling
	currentDefinition = getCurrentDefinitionAs<NativeStructure>().let {
		DeclarationRepository.update(it) {
			it.copy(fields = it.fields + (name to value))
		}
	}
}

private fun ParsingContext.updateEnumerationField(info: DeclarationInfo) {
	val name = info.cursor.spelling
	val value = info.cursor.getEnumConstantValue()
	currentDefinition = getCurrentDefinitionAs<NativeEnumeration>().let {
		DeclarationRepository.update(it) {
			it.copy(values = it.values + (name to value))
		}
	}
}

private fun ParsingContext.declareStructure(info: DeclarationInfo) {
	currentDefinition = NativeStructure(lastTypeDefName.consume() ?: info.cursor.spelling)
		.also(DeclarationRepository::save)

}

private fun ParsingContext.declareEnumeration(info: DeclarationInfo) {
	currentDefinition = NativeEnumeration(lastTypeDefName.consume() ?: info.cursor.spelling)
		.also(DeclarationRepository::save)
}

private fun ParsingContext.storeSpelling(info: DeclarationInfo) {
	lastTypeDefName.store(info.cursor.spelling)
}