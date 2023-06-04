package klang.parser.json.type

import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.isExternalDeclaration()
    = json["storageClass"]?.jsonPrimitive?.content == "extern"