package com.seizethewind.lilycode.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DraftFormatTest {
    @Test fun roundTripKeepsMultilineText() {
        val p = splitDraft(joinDraft("content://a/b", "123:45", "one\n\ntwo\n"))!!
        assertEquals("content://a/b", p.uri)
        assertEquals("123:45", p.stamp)
        assertEquals("one\n\ntwo\n", p.text)
    }

    @Test fun missingStampComesBackNull() {
        val p = splitDraft(joinDraft("u", null, "x"))!!
        assertNull(p.stamp)
        assertEquals("x", p.text)
    }

    @Test fun emptyTextSurvives() = assertEquals("", splitDraft(joinDraft("u", "s", ""))!!.text)

    @Test fun malformedIsRejected() {
        assertNull(splitDraft("no newline"))
        assertNull(splitDraft("only\none newline"))
    }
}
