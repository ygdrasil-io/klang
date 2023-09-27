package klang.jvm.binding

object CXTranslationUnit_Flags {

    val CXTranslationUnit_None = 0x0
    val CXTranslationUnit_DetailedPreprocessingRecord = 0x01
    val CXTranslationUnit_Incomplete = 0x02
    val CXTranslationUnit_PrecompiledPreamble = 0x04
    val CXTranslationUnit_CacheCompletionResults = 0x08
    val CXTranslationUnit_ForSerialization = 0x10
    val CXTranslationUnit_CXXChainedPCH = 0x20
    val CXTranslationUnit_SkipFunctionBodies = 0x40
    val CXTranslationUnit_IncludeBriefCommentsInCodeCompletion = 0x80
    val CXTranslationUnit_KeepGoing = 0x200
    val CXTranslationUnit_SingleFileParse = 0x400
}