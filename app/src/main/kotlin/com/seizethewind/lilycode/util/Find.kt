package com.seizethewind.lilycode.util

/** Start offsets of every non-overlapping, case-insensitive match of [query] in [text]. */
fun findAll(text: String, query: String): List<Int> {
    if (query.isEmpty()) return emptyList()
    val out = ArrayList<Int>()
    var i = text.indexOf(query, 0, ignoreCase = true)
    while (i >= 0) {
        out += i
        i = text.indexOf(query, i + query.length, ignoreCase = true)
    }
    return out
}
