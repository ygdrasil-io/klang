package klang.parser.json.type

import klang.domain.NativeTypeAlias
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeTypeAlias()= NativeTypeAlias(
        name = json.name(),
        type = json.type()
)