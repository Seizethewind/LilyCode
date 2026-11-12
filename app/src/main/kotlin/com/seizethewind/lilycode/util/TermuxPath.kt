package com.seizethewind.lilycode.util

object TermuxPath {
    /** Termux's documents provider; its document id is the file's absolute path. */
    const val AUTHORITY = "com.termux.documents"

    /** [path] if it is absolute, has no . or .. parts and lies inside [root], else null. */
    fun inside(path: String, root: String): String? {
        if (!path.startsWith("/") || path.split('/').any { it == ".." || it == "." }) return null
        val r = root.trimEnd('/')
        return if (path == r || path.startsWith("$r/")) path else null
    }
}
