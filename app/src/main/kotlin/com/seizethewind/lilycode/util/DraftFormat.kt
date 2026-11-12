package com.seizethewind.lilycode.util

class DraftParts(val uri: String, val stamp: String?, val text: String)

/** Draft file layout: the file's uri, a line with its disk stamp (may be empty), then the text. */
fun joinDraft(uri: String, stamp: String?, text: String) = "$uri\n${stamp.orEmpty()}\n$text"

/** Inverse of [joinDraft]; null if [s] is not a draft. The text keeps every newline it had. */
fun splitDraft(s: String): DraftParts? {
    val a = s.indexOf('\n')
    if (a <= 0) return null
    val b = s.indexOf('\n', a + 1)
    if (b < 0) return null
    return DraftParts(s.substring(0, a), s.substring(a + 1, b).ifEmpty { null }, s.substring(b + 1))
}
