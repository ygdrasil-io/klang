package klang.parser.json.domain

enum class TranslationUnitKind {
    TranslationUnitDecl, TypedefDecl, BuiltinType, RecordType, PointerType,
	ConstantArrayType, EnumDecl, FullComment, ParagraphComment, TextComment,
	InlineCommandComment, EnumConstantDecl, ConstantExpr, IntegerLiteral,
	RecordDecl, FieldDecl, ElaboratedType, FunctionDecl, ParmVarDecl,
	VisibilityAttr, ParamCommandComment, BlockCommandComment, TypedefType,
	VarDecl, AsmLabelAttr, AvailabilityAttr, EnumType, VerbatimBlockComment,
	VerbatimBlockLineComment, DeprecatedAttr, BinaryOperator, DeclRefExpr,
	UnaryOperator, ParenType, FunctionProtoType, BlockPointerType,
	VerbatimLineComment, ParenExpr,

	// Found in metal, likely common
	CompoundStmt, ReturnStmt, CStyleCastExpr, ImplicitCastExpr, CallExpr, BuiltinAttr,
	NoThrowAttr, ConstAttr, PackedAttr, MemberExpr, IfStmt, AlwaysInlineAttr,

	// Objective-C
	ObjCObjectPointerType, ObjCObjectType, ObjCInterfaceDecl,
	;

    companion object {
        fun of(value: String): TranslationUnitKind? = values().find { it.name == value }
    }
}