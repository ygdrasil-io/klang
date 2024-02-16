package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import klang.domain.NameableDeclaration
import klang.domain.NativeConstant
import klang.domain.notBlankString
import mu.KotlinLogging
import org.openjdk.jextract.Declaration

private val logger = KotlinLogging.logger {}

internal fun Declaration.Constant.toNativeConstant(origin: DeclarationOrigin): NameableDeclaration? =
	notBlankString(name())
		.also { if (it == null) logger.warn { "nameless constant at ${pos()}" } }
		?.let { it to value() }
		?.let { (name, value) ->
			when (value) {
				is String -> NativeConstant<String>(name, value, origin)
				is Long -> NativeConstant<Long>(name, value, origin)
				is Double -> NativeConstant<Double>(name, value, origin)
				else -> error("unsupported constant of type ${value::class.java.name}")
			}
		}
