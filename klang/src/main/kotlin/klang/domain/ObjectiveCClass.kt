package klang.domain

data class ObjectiveCClass(
	val name: String,
	val properties: List<String>,
	val methods: List<String>
): NativeDeclaration