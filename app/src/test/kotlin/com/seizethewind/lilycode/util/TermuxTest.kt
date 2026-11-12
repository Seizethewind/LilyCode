package com.seizethewind.lilycode.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TermuxTest {
    @Test fun pathInsideRoot() {
        assertEquals("/h/a/b.py", TermuxPath.inside("/h/a/b.py", "/h"))
        assertEquals("/h", TermuxPath.inside("/h", "/h/"))
    }

    @Test fun pathOutsideRootIsRejected() {
        assertNull(TermuxPath.inside("/hx/a", "/h"))
        assertNull(TermuxPath.inside("/h/../etc", "/h"))
        assertNull(TermuxPath.inside("rel/a", "/h"))
    }

    @Test fun pythonRunsWithPythonInFileDir() {
        val bin = "/data/data/com.termux/files/usr/bin/"
        assertEquals(RunSpec(bin + "python", listOf("/h/a.py"), "/h"), runSpec("/h/a.py"))
    }

    @Test fun goUsesRunSubcommand() = assertEquals(listOf("run", "/h/m.go"), runSpec("/h/m.go")?.args)

    @Test fun unknownExtensionHasNoRunner() = assertNull(runSpec("/h/a.txt"))
}
