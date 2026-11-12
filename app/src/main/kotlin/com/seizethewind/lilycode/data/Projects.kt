package com.seizethewind.lilycode.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.seizethewind.lilycode.util.TermuxPath

/** The remembered project list, kept in SharedPreferences as "name<TAB>uri" lines. */
class Projects(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("lily", Context.MODE_PRIVATE)

    fun all(): List<Project> = prefs.getString(KEY, "").orEmpty().lines().mapNotNull {
        val p = it.split('\t')
        if (p.size == 2) Project(p[0], p[1]) else null
    }

    fun add(p: Project): List<Project> = (all().filter { it.uri != p.uri } + p).also(::save)

    fun remove(uri: String): List<Project> = all().filter { it.uri != uri }.also(::save)

    /** The Termux project whose folder contains [path], or null. */
    fun forPath(path: String): Project? = all().firstOrNull {
        val u = Uri.parse(it.uri)
        u.authority == TermuxPath.AUTHORITY &&
            TermuxPath.inside(path, DocumentsContract.getTreeDocumentId(u)) != null
    }

    private fun save(list: List<Project>) = prefs.edit()
        .putString(KEY, list.joinToString("\n") { "${it.name.replace(Regex("[\t\n]"), " ")}\t${it.uri}" })
        .apply()

    private companion object { const val KEY = "projects" }
}
