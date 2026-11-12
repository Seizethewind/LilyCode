package com.seizethewind.lilycode.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DocPathTest {
    @Test fun underItselfAndChildrenOnly() {
        assertTrue(isUnder("/a/b", "/a/b"))
        assertTrue(isUnder("/a/b/c", "/a/b"))
        assertFalse(isUnder("/a/bc", "/a/b"))
    }

    @Test fun remapKeepsTheTail() {
        assertEquals("/x/y/c", remapId("/a/b/c", "/a/b", "/x/y"))
        assertEquals("/x", remapId("/a/b", "/a/b", "/x"))
    }

    @Test fun remapIgnoresUnrelatedIds() = assertNull(remapId("/a/bc", "/a/b", "/x"))
}
