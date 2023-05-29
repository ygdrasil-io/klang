package klang.jvm

import klang.jvm.binding.CXIdxEntityInfo

// TODO: test everything unused
@Suppress("unused")
class EntityInfo(info: CXIdxEntityInfo) {
    enum class Kind {
        UNEXPOSED,
        TYPEDEF,
        FUNCTION,
        VARIABLE,
        FIELD,
        ENUM_CONSTANT,
        OBJC_CLASS,
        OBJC_PROTOCOL,
        OBJC_CATEGORY,
        OBJC_INSTANCE_METHOD,
        OBJC_CLASS_METHOD,
        OBJC_PROPERTY,
        OBJC_IVAR,
        ENUM,
        STRUCT,
        UNION,
        CXX_CLASS,
        CXX_NAMESPACE,
        CXX_NAMESPACE_ALIAS,
        CXX_STATIC_VARIABLE,
        CXX_STATIC_METHOD,
        CXX_INSTANCE_METHOD,
        CXX_CONSTRUCTOR,
        CXX_DESTRUCTOR,
        CXX_CONVERSION_FUNCTION,
        CXX_TYPE_ALIAS,
        CXX_INTERFACE
    }

    enum class CXXTemplateKind {
        NON_TEMPLATE,
        TEMPLATE,
        TEMPLATE_PARTIAL_SPECIALIZATION,
        TEMPLATE_SPECIALIZATION
    }

    enum class Language {
        NONE,
        C,
        OBJC,
        CXX
    }

    val kind: Kind = Kind.values()[info.kind]
    val cXXTemplateKind: CXXTemplateKind = CXXTemplateKind.values()[info.templateKind]
    val language: Language = Language.values()[info.lang]
    val name: String? = info.name
    val USR: String = info.USR
    val cursor: Cursor = Cursor(info.cursor)
    val attributes: List<IndexAttribute> = IndexAttribute.createFromNative(info.attributes, info.numAttributes)

}