package klang

import klang.domain.NameableDeclaration
import klang.domain.ObjectiveCProtocol

private object ObjectiveCRootClass: NameableDeclaration {
	override val name: String = "NSObject"
}


fun DeclarationRepository.insertObjectiveCDefaultDeclaration() {
	save(ObjectiveCRootClass)
	// TODO: insert default declarations with correct method
	save(ObjectiveCProtocol("NSSecureCoding", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSXPCProxyCreating", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSLocking", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSProgressReporting", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSStream", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSProxy", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSXMLNode", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSClassDescription", setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol("NSUserScriptTask", setOf(), listOf(), listOf()))
}