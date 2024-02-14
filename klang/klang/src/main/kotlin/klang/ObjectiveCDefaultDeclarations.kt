package klang

import klang.domain.DeclarationOrigin
import klang.domain.NameableDeclaration
import klang.domain.NotBlankString
import klang.domain.ObjectiveCProtocol

object ObjectiveCRootClass: NameableDeclaration {
	override val name: NotBlankString = NotBlankString("NSObject")
	override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader
}


fun DeclarationRepository.insertObjectiveCDefaultDeclaration() {
	save(ObjectiveCRootClass)
	// TODO: insert default declarations with correct method
	save(ObjectiveCProtocol(NotBlankString("NSSecureCoding"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSXPCProxyCreating"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSLocking"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSProgressReporting"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSStream"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSProxy"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSXMLNode"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSClassDescription"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSUserScriptTask"), setOf(), listOf(), listOf()))

	save(ObjectiveCProtocol(NotBlankString("NSScanner"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSOrderedSet"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSMutableOrderedSet"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSProcessInfo"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSXMLParser"), setOf(), listOf(), listOf()))
	save(ObjectiveCProtocol(NotBlankString("NSProtocolChecker"), setOf(), listOf(), listOf()))
}