package klang.jvm

import klang.jvm.binding.CXToken
import klang.jvm.binding.CXTokenKind

class Tokens(val tokens: CXToken, val size: Int) {

    fun getTokenSegment(idx: Int): CXToken {
        return tokens
            .pointer
            .getPointer(tokens.size() * idx.toLong())
            .let(::CXToken)
    }

    fun getToken(index: Int): Token {
        return Token(getTokenSegment(index))
    }

}


class Token internal constructor(val token: CXToken) {

    fun spelling(translationUnit: TranslationUnit): String {
        return Clang.getTokenSpelling(
            translationUnit.pointer,
            token
        )
    }

}