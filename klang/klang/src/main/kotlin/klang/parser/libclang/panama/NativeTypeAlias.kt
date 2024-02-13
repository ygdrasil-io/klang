package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import klang.domain.NameableDeclaration
import klang.domain.NativeTypeAlias
import org.openjdk.jextract.Declaration

internal fun Declaration.Typedef.toNativeTypeAlias(origin: DeclarationOrigin): NameableDeclaration? = (name() to type().toTypeRef())
	.let { (name, typeRef) -> NativeTypeAlias(name, typeRef, origin) }
