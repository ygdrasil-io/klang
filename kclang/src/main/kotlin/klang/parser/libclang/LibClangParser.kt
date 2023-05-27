package klang.parser.libclang

import klang.DeclarationRepository
import klang.domain.NativeDeclaration
import klang.domain.NativeEnumeration
import klang.jvm.AbstractIndexerCallback
import klang.jvm.CursorKind
import klang.jvm.DeclarationInfo
import klang.jvm.createIndex

fun parseFile(file: String) {
	val index = createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false)
	var currentDefinition: NativeDeclaration? = null
	var lastTypeDefName: String? = null
	index.indexSourceFile(object : AbstractIndexerCallback() {
		override fun indexDeclaration(info: DeclarationInfo) {
			when (info.cursor.kind) {
				CursorKind.TYPEDEF_DECL -> {
					lastTypeDefName = info.cursor.spelling
				}
				CursorKind.ENUM_DECL -> {
					currentDefinition = NativeEnumeration(lastTypeDefName ?: info.cursor.spelling)
						.also(DeclarationRepository::save)
					lastTypeDefName = null
				}
				CursorKind.ENUM_CONSTANT_DECL -> {
					val name = info.cursor.spelling
					val value = info.cursor.getEnumConstantValue()
					currentDefinition = (currentDefinition as? NativeEnumeration)
						.also { if(it == null) throw IllegalStateException("Expected NativeEnumeration") }
						?.let {
							DeclarationRepository.update(it) {
								it.copy(values = it.values + (name to value))
							}
						}


				}
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