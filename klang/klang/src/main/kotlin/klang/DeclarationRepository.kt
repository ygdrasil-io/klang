package klang

import klang.domain.*

val libraryDeclarationsFilter: (ResolvableDeclaration) -> Boolean  = { resolvableDeclaration ->
	when (resolvableDeclaration) {
		is SourceableDeclaration -> (resolvableDeclaration.source is DeclarationOrigin.LibraryHeader)
		else -> false
	}
}

interface DeclarationRepository {

	val declarations: Set<NativeDeclaration>

	fun save(declaration: NameableDeclaration)
	fun clear()
	fun update(nativeEnumeration: NativeDeclaration, provider: () -> NativeDeclaration): NativeDeclaration
	fun resolveTypes(filter: (ResolvableDeclaration) -> Boolean = libraryDeclarationsFilter)

	fun findEnumerationByName(name: String) = findDeclarationByName<NativeEnumeration>(name)

	fun findStructureByName(name: String) = findDeclarationByName<NativeStructure>(name)

	fun findFunctionByName(name: String) = findDeclarationByName<NativeFunction>(name)

	fun findTypeAliasByName(name: String) = findDeclarationByName<NativeTypeAlias>(name)

	fun findObjectiveCClassByName(name: String) = findDeclarationByName<ObjectiveCClass>(name)

	fun findObjectiveCProtocolByName(name: String) = findDeclarationByName<ObjectiveCProtocol>(name)

	fun findObjectiveCCategoryByName(name: String) = findDeclarationByName<ObjectiveCCategory>(name)

	fun findLibraryDeclaration() = declarations.asSequence()
		.filterIsInstance<SourceableDeclaration>()
		.filter { it.source is DeclarationOrigin.LibraryHeader }
		.toList()
}

inline fun <reified T : NameableDeclaration> DeclarationRepository.findDeclarationByName(declarationName: String) = declarations
	.asSequence()
	.filterIsInstance<T>()
	.firstOrNull { it.name == declarationName }