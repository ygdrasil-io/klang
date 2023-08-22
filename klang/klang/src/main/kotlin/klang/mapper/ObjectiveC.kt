package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeStructure
import klang.domain.ObjectiveCClass

internal fun ObjectiveCClass.toSpec() = ClassName("", name)
	.let { structureClass ->
		TypeSpec.classBuilder(structureClass)
			.superclass(nsobjectDefinition)
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter(
						ParameterSpec.builder("id", Long::class)
							.build()
					)
					.build()
			)
			.addSuperclassConstructorParameter("id")
			.apply {


			}
			.build()
	}