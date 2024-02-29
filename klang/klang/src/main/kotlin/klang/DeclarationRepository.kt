package klang

import klang.domain.*

val libraryDeclarationsFilter: (ResolvableDeclaration) -> Boolean  = { resolvableDeclaration ->
	when (resolvableDeclaration) {
		is SourceableDeclaration -> (resolvableDeclaration.source is DeclarationOrigin.LibraryHeader)
		else -> false
	}
}

val allDeclarationsFilter: (ResolvableDeclaration) -> Boolean  = { _ -> true }

/**
 * The DeclarationRepository interface represents a repository for native declarations From C headers so far.
 */
interface DeclarationRepository {

	/**
	 * Set of native C/C++ or Objective-C declarations.
	 */
	val declarations: Set<NativeDeclaration>

	/**
	 * Saves the given [declaration] in the DeclarationRepository.
	 * Some native declaration support merging, if the definition exist, they will be merged
	 *
	 * @param declaration The nameable declaration to be saved.
	 */
	fun save(declaration: NameableDeclaration)

	/**
	 * Remove all declaration of the DeclarationRepository.
	 */
	fun clear()
	fun update(nativeEnumeration: NativeDeclaration, provider: () -> NativeDeclaration): NativeDeclaration
	fun resolveTypes(filter: (ResolvableDeclaration) -> Boolean = libraryDeclarationsFilter)
	fun resolveAllTypes() = resolveTypes(allDeclarationsFilter)

	fun findConstantByName(name: String) = findConstantByName(NotBlankString(name))
	fun findConstantByName(name: NotBlankString) = findDeclarationByName<NativeConstant<*>>(name)

	fun findEnumerationByName(name: String) = findEnumerationByName(NotBlankString(name))
	fun findEnumerationByName(name: NotBlankString) = findDeclarationByName<NativeEnumeration>(name)

	fun findStructureByName(name: String) = findStructureByName(NotBlankString(name))
	fun findStructureByName(name: NotBlankString) = findDeclarationByName<NativeStructure>(name)

	fun findFunctionByName(name: String) = findFunctionByName(NotBlankString(name))
	fun findFunctionByName(name: NotBlankString) = findDeclarationByName<NativeFunction>(name)

	fun findTypeAliasByName(name: String) = findTypeAliasByName(NotBlankString(name))
	fun findTypeAliasByName(name: NotBlankString) = findDeclarationByName<NativeTypeAlias>(name)

	fun findObjectiveCClassByName(name: String) = findObjectiveCClassByName(NotBlankString(name))
	fun findObjectiveCClassByName(name: NotBlankString) = findDeclarationByName<ObjectiveCClass>(name)

	fun findObjectiveCProtocolByName(name: String) = findObjectiveCProtocolByName(NotBlankString(name))
	fun findObjectiveCProtocolByName(name: NotBlankString) = findDeclarationByName<ObjectiveCProtocol>(name)

	fun findObjectiveCCategoryByName(name: String) = findObjectiveCCategoryByName(NotBlankString(name))
	fun findObjectiveCCategoryByName(name: NotBlankString) = findDeclarationByName<ObjectiveCCategory>(name)

	fun findLibraryDeclaration() = declarations.asSequence()
		.filterIsInstance<SourceableDeclaration>()
		.filter { it.source is DeclarationOrigin.LibraryHeader }
		.toList()
}


inline fun <reified T : NameableDeclaration> DeclarationRepository.findDeclarationByName(declarationName: String) = findDeclarationByName<T>(NotBlankString(declarationName))

inline fun <reified T : NameableDeclaration> DeclarationRepository.findDeclarationByName(declarationName: NotBlankString) = declarations
	.asSequence()
	.filterIsInstance<T>()
	.firstOrNull { it.name == declarationName }