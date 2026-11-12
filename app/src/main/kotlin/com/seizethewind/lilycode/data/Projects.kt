package com.seizethewind.lilycode.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.seizethewind.lilycode.util.TermuxPath

/** The remembered project list, kept in SharedPreferences as "name<TAB>uri" lines, plus each project's open tabs. */
class Projects(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("lily", Context.MODE_PRIVATE)

    fun all(): List<Project> = prefs.getString(KEY, "").orEmpty().lines().mapNotNull {
        val p = it.split('\t')
        if (p.size == 2) Project(p[0], p[1]) else null
    }

    fun add(p: Project): List<Project> = (all().filter { it.uri != p.uri } + p).also(::save)

    fun remove(uri: String): List<Project> {
        prefs.edit().remove("tabs:$uri").apply()
        return all().filter { it.uri != uri }.also(::save)
    }

    /** The Termux project whose folder contains [path], or null. */
    fun forPath(path: String): Project? = all().firstOrNull {
        val u = Uri.parse(it.uri)
        u.authority == TermuxPath.AUTHORITY &&
            TermuxPath.inside(path, DocumentsContract.getTreeDocumentId(u)) != null
    }

    /** Open file uris of [p] and the index of the current one (-1 for none), as last saved. */
    fun session(p: Project): Pair<List<String>, Int> {
        val lines = prefs.getString("tabs:${p.uri}", "").orEmpty().lines()
        return lines.drop(1).filter { it.isNotEmpty() } to (lines.firstOrNull()?.toIntOrNull() ?: -1)
    }

    fun saveSession(p: Project, uris: List<String>, current: Int) = prefs.edit()
        .putString("tabs:${p.uri}", (listOf(current.toString()) + uris).joinToString("\n"))
        .apply()

    private fun save(list: List<Project>) = prefs.edit()
        .putString(KEY, list.joinToString("\n") { "${it.name.replace(Regex("[\t\n]"), " ")}\t${it.uri}" })
        .apply()

    private companion object { const val KEY = "projects" }
}
