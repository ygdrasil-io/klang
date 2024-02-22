package klang.parser.libclang

import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.DeclarationOrigin
import klang.domain.NameableDeclaration
import klang.domain.NativeConstant
import klang.domain.notBlankString
import klang.parse
import klang.parser.libclang.panama.OriginProcessor.toOrigin
import klang.parser.libclang.panama.toNativeConstant
import klang.parser.libclang.panama.toNativeEnumeration
import klang.parser.libclang.panama.toNativeStructure
import klang.parser.libclang.panama.toNativeTypeAlias
import mu.KotlinLogging
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.Declaration.Scoped
import org.openjdk.jextract.Declaration.Typedef
import org.openjdk.jextract.impl.TypeImpl
import java.nio.file.Path
import kotlin.io.path.pathString

private val logger = KotlinLogging.logger {}

fun DeclarationRepository.parseFileWithPanama(file: String, filePath: Path?, headerPaths: Array<Path>, macros: Map<String, String?>): DeclarationRepository = apply {
		val header = Path.of(file)

		var clangArguments = filePath?.let { "-I${it.toFile().absolutePath}" }
			?.let { arrayOf(it) }
			?: arrayOf()
		clangArguments += headerPaths.map { "-I${it.toFile().absolutePath}" }

	clangArguments += macros.map { (key, value) -> "-D${key}${value?.let { "=$it" } ?: ""}" }

		val topLevel = parse(
			listOf(header),
			*clangArguments
		)

		check(topLevel.kind() == Declaration.Scoped.Kind.TOPLEVEL)

		topLevel.members()
			.asSequence()
			.filter { it.declarationIsOnFilePath(filePath) }
			.map {
				val origin = it.pos().toOrigin(filePath)
				when (it) {
					is Scoped -> it.scopedToLocalDeclaration(origin = origin)
					is Typedef -> it.typeDefToLocalDeclaration(origin)
					is Declaration.Function -> it.toNativeTypeAlias(origin)
					is Declaration.Constant -> it.toNativeConstant(origin)
					else -> {
						logger.error { "not found $it" }
						null
					}
				}
			}
			.filterNotNull()
			.forEach { save(it) }

	}



internal fun Declaration.declarationIsOnFilePath(filePath: Path?): Boolean = filePath
	?.pathString
	?.let { pos().path().parent.pathString.contains(it) } ?: true

private fun Typedef.typeDefToLocalDeclaration(origin: DeclarationOrigin): NameableDeclaration? = type().let { type ->
	when (type) {
		is TypeImpl.DeclaredImpl -> type.tree().scopedToLocalDeclaration(name(), origin)
		else -> toNativeTypeAlias(origin)
	}
}

private fun Scoped.scopedToLocalDeclaration(name: String? = null, origin: DeclarationOrigin): NameableDeclaration? {
	return when (kind()) {
		Declaration.Scoped.Kind.ENUM -> toNativeEnumeration(name, origin)
		Declaration.Scoped.Kind.STRUCT -> toNativeStructure(name, origin = origin)
		Declaration.Scoped.Kind.UNION -> toNativeStructure(name, isUnion = true, origin)

		else -> {
			logger.error { "not found ${kind()}" }
			null
		}
	}
}