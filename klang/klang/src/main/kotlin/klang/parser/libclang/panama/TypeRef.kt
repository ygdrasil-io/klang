package klang.parser.libclang.panama

import klang.domain.*
import org.openjdk.jextract.Type
import org.openjdk.jextract.Type.Delegated
import org.openjdk.jextract.impl.TypeImpl
import java.lang.foreign.ValueLayout

internal fun Type.toTypeRef(): TypeRef = when (this) {
	is Delegated -> when (kind()) {
		Delegated.Kind.TYPEDEF -> typeOf(name().get()).unchecked()
		Delegated.Kind.POINTER -> type().let { type ->
			when (type) {
				is TypeImpl.FunctionImpl -> UnresolvedTypeRef(
					toString(),
					NotBlankString(type.toTypeString()),
					isPointer = true,
					isCallback = true
				)
				else -> type.toTypeString().removeConstPrefix().removePointerSuffix()
					.let { typeName -> UnresolvedTypeRef(toString(), NotBlankString(typeName), isPointer = true)  }
			}
		}

		Delegated.Kind.SIGNED -> typeOf(type().toTypeString()).unchecked()
		Delegated.Kind.UNSIGNED -> typeOf("unsigned " + type().toTypeString()).unchecked()
		Delegated.Kind.ATOMIC -> TODO("unsupported yet")
		Delegated.Kind.VOLATILE -> TODO("unsupported yet")
		Delegated.Kind.COMPLEX -> TODO("unsupported yet")
		null -> TODO("unsupported yet")
	}

	is TypeImpl.FunctionImpl -> returnType().toTypeRef()
	is TypeImpl.PrimitiveImpl -> typeOf(toTypeString()).unchecked()
	is TypeImpl.ArrayImpl -> typeOf(toTypeString()).unchecked()
	is TypeImpl.DeclaredImpl -> typeOf(toTypeString()).unchecked()
	else -> TODO("unsupported yet")
}

//TODO find a better way to handle this
private fun String.removePointerSuffix(): String = removeSuffix(" *")

//TODO find a better way to handle this
private fun String.removeConstPrefix(): String = removePrefix("const ")

private fun Type.toTypeString(): String = when (this) {
	is TypeImpl.DeclaredImpl -> toTypeString()
	is TypeImpl.PrimitiveImpl -> kind().typeName()
	is TypeImpl.QualifiedImpl -> name()
		.orElseGet { type().toTypeString() }

	is TypeImpl.ArrayImpl -> elementType().toTypeString()
		.let { typeAsString -> countElements()?.let { "$typeAsString[$it]" } ?: "$typeAsString[]" }

	is TypeImpl.FunctionImpl -> functionToTypeString()
	is TypeImpl.PointerImpl -> "${type().toTypeString()} *"
	else -> TODO("unsupported yet with $this")
}

private fun TypeImpl.FunctionImpl.functionToTypeString(): String {
	return returnType().toTypeString() + "( ${argumentTypes().toTypeString()} )"
}

private fun List<Type>.toTypeString(): String = map {
	it.toTypeRef().typeName
}.joinToString(",")

@Suppress("INACCESSIBLE_TYPE")
private fun TypeImpl.DeclaredImpl.toTypeString(): String = tree().name()
	.takeIf { it.isNotEmpty() }
// if declared name is not accessible, we try to get type name directly
	?: tree().layout()?.orElse(null)?.let {
		when {
			it is ValueLayout.OfInt && it.byteSize() == 4L -> "int"
			else -> error("fail to get type name from type ${it.javaClass.name}")
		}
	}
	?: error("fail to get type name")

private fun TypeImpl.ArrayImpl.countElements() = elementCount().let { elementCount ->
	when (elementCount.isEmpty) {
		true -> null
		else -> elementCount.asLong
	}

}