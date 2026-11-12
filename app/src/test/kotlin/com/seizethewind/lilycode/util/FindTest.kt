package com.seizethewind.lilycode.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FindTest {
    @Test fun ignoresCase() = assertEquals(listOf(0, 1, 2), findAll("aAa", "a"))

    @Test fun matchesDoNotOverlap() = assertEquals(listOf(0, 2), findAll("abab", "ab"))

    @Test fun emptyQueryFindsNothing() = assertEquals(emptyList<Int>(), findAll("abc", ""))
}
