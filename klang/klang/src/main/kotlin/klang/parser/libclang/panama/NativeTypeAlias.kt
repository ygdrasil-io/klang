package klang.parser.libclang.panama

import klang.domain.NameableDeclaration
import klang.domain.NativeTypeAlias
import org.openjdk.jextract.Declaration

internal fun Declaration.Typedef.toNativeTypeAlias(): NameableDeclaration?
	= NativeTypeAlias(name(), type().toTypeRef())
