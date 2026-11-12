package com.seizethewind.lilycode.util

enum class Kind { Keyword, Str, Comment, Number }

class Span(val start: Int, val end: Int, val kind: Kind)

// One regex per language; at each position the first alternative wins, so a keyword inside a string or comment is never matched.
// shortcut: no triple-quoted strings, no nesting, no hex floats; upgrade to a real lexer if highlighting must be exact.
private fun lang(kw: String, line: String?, block: Boolean = false): Regex {
    val comment = listOfNotNull(if (block) """/\*[\s\S]*?\*/""" else null, line?.let { "$it.*" })
        .joinToString("|").ifEmpty { "(?!)" }
    val words = kw.replace(' ', '|')
    return Regex("""(?<c>$comment)|(?<s>"(?:\\.|[^"\\\n])*"|'(?:\\.|[^'\\\n])*')|(?<n>\b\d[\w.]*)|(?<k>\b(?:$words)\b)""")
}

private val py = lang("def class return if elif else for while in not and or is None True False import from as with try except finally raise pass break continue lambda yield global del assert async await", "#")
private val sh = lang("if then else elif fi for while do done case esac function in return exit local export", "#")
private val js = lang("const let var function return if else for while do switch case break continue class extends new this null undefined true false import export from default async await try catch finally throw typeof instanceof of in", "//", true)
private val kt = lang("fun val var class object interface return if else for while when do break continue null true false import package is in as try catch finally throw override private public protected static final void new this super", "//", true)
private val c = lang("if else for while do switch case break continue return struct enum typedef static const void int char float double long unsigned sizeof NULL true false class public private new delete this namespace using include", "//", true)
private val go = lang("func var const type struct interface return if else for range switch case break continue package import go defer chan map nil true false", "//", true)
private val rs = lang("fn let mut const struct enum impl trait pub use mod return if else for while loop match break continue self Self true false as in ref move async await", "//", true)
private val json = lang("true false null", null)

private val byExt = mapOf(
    "py" to py, "sh" to sh, "bash" to sh, "js" to js, "ts" to js, "kt" to kt, "java" to kt,
    "c" to c, "h" to c, "cpp" to c, "go" to go, "rs" to rs, "json" to json,
)

fun tokenize(text: String, fileName: String): List<Span> {
    val re = byExt[fileName.substringAfterLast('.', "").lowercase()] ?: return emptyList()
    if (text.length > 100_000) return emptyList() // shortcut: skip huge files, upgrade to incremental highlighting
    return re.findAll(text).map { m ->
        val kind = when {
            m.groups["c"] != null -> Kind.Comment
            m.groups["s"] != null -> Kind.Str
            m.groups["n"] != null -> Kind.Number
            else -> Kind.Keyword
        }
        Span(m.range.first, m.range.last + 1, kind)
    }.toList()
}
