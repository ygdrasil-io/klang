package klang.jvm

import klang.jvm.binding.CXIdxDeclInfo

class DeclarationInfo(info: CXIdxDeclInfo) {
	val entityInfo: EntityInfo = EntityInfo(info.entityInfo)
	val cursor: Cursor = Cursor(info.cursor)
	val location: IndexLocation = IndexLocation(info.loc)
	val semanticContainer: ContainerInfo = ContainerInfo(info.semanticContainer)
	val lexicalContainer: ContainerInfo = ContainerInfo(info.lexicalContainer)
	val isRedeclaration: Boolean = info.isRedeclaration
	val isDefinition: Boolean = info.isDefinition
	val isContainer: Boolean = info.isContainer
	val declAsContainer: ContainerInfo? =
		if (info.declAsContainer == null) null else ContainerInfo(info.declAsContainer)
	val isImplicit: Boolean = info.isImplicit
	val attributes: List<IndexAttribute> = IndexAttribute.createFromNative(info.attributes, info.numAttributes)

}