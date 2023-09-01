package klang.parser.json.type

import arrow.core.getOrElse
import klang.domain.NativeTypeAlias
import klang.domain.typeOf
import klang.domain.unchecked
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json

internal fun TranslationUnitNode.toNativeTypeAlias()= NativeTypeAlias(
        name = json.name(),
        type = json.type().let(::typeOf).unchecked("fail to parse type $this")
)