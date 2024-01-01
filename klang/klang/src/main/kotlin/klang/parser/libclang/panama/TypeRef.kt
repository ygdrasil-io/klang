package klang.parser.libclang.panama

import klang.domain.TypeRef
import klang.domain.typeOf
import klang.domain.unchecked
import org.openjdk.jextract.Type
import org.openjdk.jextract.Type.Delegated
import org.openjdk.jextract.impl.TypeImpl

internal fun Type.toTypeRef(): TypeRef = when (this) {
	is Delegated -> when (kind()) {
		Delegated.Kind.TYPEDEF -> typeOf(name().get()).unchecked()
		Delegated.Kind.POINTER -> typeOf(type().toTypeString() + " *").unchecked()
		Delegated.Kind.SIGNED -> TODO("unsupported yet")
		Delegated.Kind.UNSIGNED -> TODO("unsupported yet")
		Delegated.Kind.ATOMIC -> TODO("unsupported yet")
		Delegated.Kind.VOLATILE -> TODO("unsupported yet")
		Delegated.Kind.COMPLEX -> TODO("unsupported yet")
		null -> TODO("unsupported yet")
	}

	is TypeImpl.FunctionImpl -> returnType().toTypeRef()
	is TypeImpl.PrimitiveImpl -> typeOf(toTypeString()).unchecked()
	else -> typeOf(toTypeString() + " *").unchecked()
}

private fun Type.toTypeString(): String = when (this) {
	is TypeImpl.DeclaredImpl -> toTypeString()
	is TypeImpl.PrimitiveImpl -> kind().typeName()
	is TypeImpl.QualifiedImpl -> name().orElse(type().toTypeString())
	else -> TODO("unsupported yet")
}

private fun TypeImpl.DeclaredImpl.toTypeString(): String = tree().name()
