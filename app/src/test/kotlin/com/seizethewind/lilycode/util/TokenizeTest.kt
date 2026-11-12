package com.seizethewind.lilycode.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TokenizeTest {
    private fun kinds(text: String, file: String) = tokenize(text, file).map { it.kind }

    @Test fun keywordInsideStringIsNotAKeyword() {
        assertEquals(listOf(Kind.Keyword, Kind.Str, Kind.Comment), kinds("val x = \"if\" // note", "a.kt"))
    }

    @Test fun pythonHashComment() {
        assertEquals(listOf(Kind.Number, Kind.Comment), kinds("x = 1  # if", "a.py"))
    }

    @Test fun blockCommentSpansLines() {
        val spans = tokenize("a /* if\nelse */ b", "a.c")
        assertEquals(1, spans.size)
        assertEquals(Kind.Comment, spans[0].kind)
    }

    @Test fun unknownExtensionHasNoSpans() {
        assertEquals(emptyList<Kind>(), kinds("if x", "notes.txt"))
    }
}
