package klang

import klang.domain.*

interface DeclarationRepository {

	val declarations: Set<NativeDeclaration>

	fun save(declaration: NameableDeclaration)
	fun clear()
	fun update(nativeEnumeration: NativeDeclaration, provider: () -> NativeDeclaration): NativeDeclaration
	fun DeclarationRepository.resolve()

	fun findEnumerationByName(name: String) = findDeclarationByName<NativeEnumeration>(name)

	fun findStructureByName(name: String) = findDeclarationByName<NativeStructure>(name)

	fun findFunctionByName(name: String) = findDeclarationByName<NativeFunction>(name)

	fun findTypeAliasByName(name: String) = findDeclarationByName<NativeTypeAlias>(name)

	fun findObjectiveCClassByName(name: String) = findDeclarationByName<ObjectiveCClass>(name)

	fun findObjectiveCProtocolByName(name: String) = findDeclarationByName<ObjectiveCProtocol>(name)

	fun findObjectiveCCategoryByName(name: String) = findDeclarationByName<ObjectiveCCategory>(name)

	fun findDeclarationsByName(declarationName: String) = declarations
		.asSequence()
		.filterIsInstance<NameableDeclaration>()
		.filter { it.name == declarationName }
		.toList()

}

inline fun <reified T : NameableDeclaration> DeclarationRepository.findDeclarationByName(declarationName: String) = declarations
	.asSequence()
	.filterIsInstance<T>()
	.firstOrNull { it.name == declarationName }