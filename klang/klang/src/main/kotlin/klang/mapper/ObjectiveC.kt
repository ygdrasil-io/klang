package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
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