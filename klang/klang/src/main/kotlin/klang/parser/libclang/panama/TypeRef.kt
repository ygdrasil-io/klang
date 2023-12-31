package klang.parser.libclang.panama

import klang.domain.TypeRef
import klang.domain.typeOf
import klang.domain.unchecked
import org.openjdk.jextract.Type
import org.openjdk.jextract.impl.TypeImpl

internal fun Type.toTypeRef(): TypeRef = when (this) {
	is TypeImpl.PointerImpl -> typeOf( type().toTypeString() + " *" ).unchecked("unsupported yet")
	is TypeImpl.FunctionImpl -> returnType().toTypeRef()
	else -> typeOf( toTypeString() + " *" ).unchecked()
}

private fun Type.toTypeString(): String = when (this) {
	is TypeImpl.DeclaredImpl -> tree().name()
	is TypeImpl.PrimitiveImpl -> kind().typeName()
	is TypeImpl.QualifiedImpl -> type().toTypeString()
	else -> TODO("unsupported yet")
}
