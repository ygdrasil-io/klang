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
	VerbatimLineComment, ParenExpr, ImportDecl, VectorType, NoDebugAttr, TargetAttr,
	MinVectorWidthAttr,
	CompoundLiteralExpr,
	ShuffleVectorExpr,
	MayAliasAttr,
	ConvertVectorExpr,
	StaticAssertDecl,
	StringLiteral,
	RecoveryExpr,
	DoStmt,
	AttributedStmt,
	FallThroughAttr,
	NullStmt,
	AnalyzerNoReturnAttr,
	ModeAttr,

	// Found in metal, likely common
	CompoundStmt, ReturnStmt, CStyleCastExpr, ImplicitCastExpr, CallExpr, BuiltinAttr,
	NoThrowAttr, ConstAttr, PackedAttr, MemberExpr, IfStmt, AlwaysInlineAttr,
	ArraySubscriptExpr, UnaryExprOrTypeTraitExpr, CompoundAssignOperator, ColdAttr,
	DisableTailCallsAttr, ConditionalOperator, DeclStmt, PureAttr, FloatingLiteral,
	ReturnsTwiceAttr, FormatAttr, CharacterLiteral, FormatArgAttr, WarnUnusedResultAttr,
	AllocSizeAttr, AllocAlignAttr, NoEscapeAttr, MaxFieldAlignmentAttr, QualType,
	EnumExtensibilityAttr, SwitchStmt, CaseStmt, DefaultStmt,
	BreakStmt, FlagEnumAttr, IndirectFieldDecl, AttributedType, UnavailableAttr,
	EmptyDecl, NonNullAttr, RestrictAttr, NotTailCalledAttr, SentinelAttr,
	InitListExpr, UnusedAttr, AlignedAttr, UsedAttr, IncompleteArrayType,
	DecayedType,

	// Mac NS
	NSReturnsRetainedAttr, NSConsumedAttr, NSConsumesSelfAttr, NSErrorDomainAttr,
	// Mac CF
	CFConsumedAttr, GCCAsmStmt, CFReturnsRetainedAttr, CFReturnsNotRetainedAttr,

	// Swift
	SwiftNewTypeAttr, SwiftNameAttr, SwiftAttrAttr, SwiftPrivateAttr, SwiftErrorAttr,
	SwiftAsyncAttr, SwiftAsyncNameAttr, SwiftBridgedTypedefAttr,

	// Objective-C ARC
	ArcWeakrefUnavailableAttr,
	WeakImportAttr,

	// Objective-C
	ObjCObjectPointerType, ObjCObjectType, ObjCInterfaceDecl, ObjCBridgeAttr, ObjCBridgeMutableAttr,
	ObjCBoxableAttr, ObjCProtocolDecl, ObjCMethodDecl, ObjCPropertyDecl, ObjCRootClassAttr,
	ObjCIvarDecl, ObjCDesignatedInitializerAttr, ObjCInterfaceType, ObjCIndependentClassAttr,
	ObjCCategoryDecl, ObjCMessageExpr, ObjCTypeParamDecl, ObjCReturnsInnerPointerAttr,
	ObjCBoolLiteralExpr, ObjCExceptionAttr, ObjCExplicitProtocolImplAttr, ObjCBridgeRelatedAttr,
	IBActionAttr, IBOutletAttr, IBOutletCollectionAttr, ObjCSubscriptRefExpr, ObjCPropertyImplDecl,
	ObjCRequiresSuperAttr, ObjCRequiresPropertyDefsAttr,
	;

    companion object {
        fun of(value: String): TranslationUnitKind? = values().find { it.name == value }
    }
}