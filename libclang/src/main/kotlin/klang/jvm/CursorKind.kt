package klang.jvm

import java.util.*

@Suppress("unused")
enum class CursorKind {
    UNEXPOSED_DECL,
    STRUCT_DECL,
    UNION_DECL,
    CLASS_DECL,
    ENUM_DECL,
    FIELD_DECL,
    ENUM_CONSTANT_DECL,
    FUNCTION_DECL,
    VAR_DECL,
    PARM_DECL,
    OBJC_INTERFACE_DECL,
    OBJC_CATEGORY_DECL,
    OBJC_PROTOCOL_DECL,
    OBJC_PROPERTY_DECL,
    OBJC_IVAR_DECL,
    OBJC_INSTANCE_METHOD_DECL,
    OBJC_CLASS_METHOD_DECL,
    OBJC_IMPLEMENTATION_DECL,
    OBJC_CATEGORY_IMPL_DECL,
    TYPEDEF_DECL,
    CXX_METHOD,
    NAMESPACE,
    LINKAGE_SPEC,
    CONSTRUCTOR,
    DESTRUCTOR,
    CONVERSION_FUNCTION,
    TEMPLATE_TYPE_PARAMETER,
    NON_TYPE_TEMPLATE_PARAMETER,
    TEMPLATE_TEMPLATE_PARAMETER,
    FUNCTION_TEMPLATE,
    CLASS_TEMPLATE,
    CLASS_TEMPLATE_PARTIAL_SPECIALIZATION,
    NAMESPACE_ALIAS,
    USING_DIRECTIVE,
    USING_DECLARATION,
    TYPE_ALIAS_DECL,
    OBJC_SYNTHESIZE_DECL,
    OBJC_DYNAMIC_DECL,
    CXX_ACCESS_SPECIFIER,
    OBJC_SUPER_CLASS_REF,
    OBJC_PROTOCOL_REF,
    OBJC_CLASS_REF,
    TYPE_REF,
    CXX_BASE_SPECIFIER,
    TEMPLATE_REF,
    NAMESPACE_REF,
    MEMBER_REF,
    LABEL_REF,
    OVERLOADED_DECL_REF,
    VARIABLE_REF,
    INVALID_FILE,
    NO_DECL_FOUND,
    NOT_IMPLEMENTED,
    INVALID_CODE,
    UNEXPOSED_EXPR,
    DECL_REF_EXPR,
    MEMBER_REF_EXPR,
    CALL_EXPR,
    OBJC_MESSAGE_EXPR,
    BLOCK_EXPR,
    INTEGER_LITERAL,
    FLOATING_LITERAL,
    IMAGINARY_LITERAL,
    STRING_LITERAL,
    CHARACTER_LITERAL,
    PAREN_EXPR,
    UNARY_OPERATOR,
    ARRAY_SUBSCRIPT_EXPR,
    BINARY_OPERATOR,
    COMPOUND_ASSIGN_OPERATOR,
    CONDITIONAL_OPERATOR,
    C_STYLE_CAST_EXPR,
    COMPOUND_LITERAL_EXPR,
    INIT_LIST_EXPR,
    ADDR_LABEL_EXPR,
    STMT_EXPR,
    GENERIC_SELECTION_EXPR,
    GNU_NULL_EXPR,
    CXX_STATIC_CAST_EXPR,
    CXX_DYNAMIC_CAST_EXPR,
    CXX_REINTERPRET_CAST_EXPR,
    CXX_CONST_CAST_EXPR,
    CXX_FUNCTIONAL_CAST_EXPR,
    CXX_TYPEID_EXPR,
    CXX_BOOL_LITERAL_EXPR,
    CXX_NULL_PTR_LITERAL_EXPR,
    CXX_THIS_EXPR,
    CXX_THROW_EXPR,
    CXX_NEW_EXPR,
    CXX_DELETE_EXPR,
    UNARY_EXPR,
    OBJC_STRING_LITERAL,
    OBJC_ENCODE_EXPR,
    OBJC_SELECTOR_EXPR,
    OBJC_PROTOCOL_EXPR,
    OBJC_BRIDGED_CAST_EXPR,
    PACK_EXPANSION_EXPR,
    SIZE_OF_PACK_EXPR,
    LAMBDA_EXPR,
    OBJC_BOOL_LITERAL_EXPR,
    UNEXPOSED_STMT,
    LABEL_STMT,
    COMPOUND_STMT,
    CASE_STMT,
    DEFAULT_STMT,
    IF_STMT,
    SWITCH_STMT,
    WHILE_STMT,
    DO_STMT,
    FOR_STMT,
    GOTO_STMT,
    INDIRECT_GOTO_STMT,
    CONTINUE_STMT,
    BREAK_STMT,
    RETURN_STMT,
    GCC_ASM_STMT,

    // ASM_STMT,
    OBJC_AT_TRY_STMT,
    OBJC_AT_CATCH_STMT,
    OBJC_AT_FINALLY_STMT,
    OBJC_AT_THROW_STMT,
    OBJC_AT_SYNCHRONIZED_STMT,
    OBJC_AUTORELEASE_POOL_STMT,
    OBJC_FOR_COLLECTION_STMT,
    CXX_CATCH_STMT,
    CXX_TRY_STMT,
    CXX_FOR_RANGE_STMT,
    SEH_TRY_STMT,
    SEH_EXCEPT_STMT,
    SEH_FINALLY_STMT,
    MS_ASM_STMT,
    NULL_STMT,
    DECL_STMT,
    TRANSLATION_UNIT,
    UNEXPOSED_ATTR,
    IB_ACTION_ATTR,
    IB_OUTLET_ATTR,
    IB_OUTLET_COLLECTION_ATTR,
    CXX_FINAL_ATTR,
    CXX_OVERRIDE_ATTR,
    ANNOTATE_ATTR,
    ASM_LABEL_ATTR,
    PREPROCESSING_DIRECTIVE,
    MACRO_DEFINITION,
    MACRO_EXPANSION,

    // MACRO_INSTANTIATION,
    INCLUSION_DIRECTIVE,
    MODULE_IMPORT_DECL;

    fun toNative(): Int {
        return TO_NATIVE[this]
            ?: throw IllegalStateException("No corresponding CXCursorKind value: $this. Probably an incompatible libclang version")
    }

    val spelling: String
        get() = Clang.getCursorKindSpelling(toNative())

    companion object {
        private val FROM_NATIVE: MutableMap<Int, CursorKind> = HashMap()
        private val TO_NATIVE: MutableMap<CursorKind, Int> = EnumMap(
            CursorKind::class.java
        )

        init {
            // The code below depends on the actual CXCursorKind enum values
            var nativeValue = 1
            for (enumValue in values()) {
                nativeValue = when (enumValue) {
                    INVALID_FILE -> 70
                    UNEXPOSED_EXPR -> 100
                    UNEXPOSED_STMT -> 200
                    TRANSLATION_UNIT -> 300
                    UNEXPOSED_ATTR -> 400
                    PREPROCESSING_DIRECTIVE -> 500
                    MODULE_IMPORT_DECL -> 600
                    else -> nativeValue
                }
                FROM_NATIVE[nativeValue] = enumValue
                TO_NATIVE[enumValue] = nativeValue
                nativeValue++
            }
        }

        fun fromNative(kind: Int): CursorKind {
            return FROM_NATIVE[kind]
                ?: throw IllegalStateException("Unknown CXCursorKind value: $kind. Probably an incompatible libclang version")
        }
    }
}