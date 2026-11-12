package com.seizethewind.lilycode.util

/** True if [id] is [parent] itself or lies under it. Only meaningful for path-like document ids (Termux, local storage). */
fun isUnder(id: String, parent: String) = id == parent || id.startsWith("$parent/")

/** [id] after [old] was renamed or moved to [dest], or null if [id] was not under [old]. */
fun remapId(id: String, old: String, dest: String): String? =
    if (isUnder(id, old)) dest + id.substring(old.length) else null
