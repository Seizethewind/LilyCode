package com.seizethewind.lilycode.util

class RunSpec(val exe: String, val args: List<String>, val dir: String) {
    override fun equals(other: Any?) = other is RunSpec && exe == other.exe && args == other.args && dir == other.dir
    override fun hashCode() = listOf(exe, args, dir).hashCode()
}

private const val BIN = "/data/data/com.termux/files/usr/bin/"

private val runners = mapOf(
    "py" to listOf("python"), "sh" to listOf("bash"), "js" to listOf("node"), "rb" to listOf("ruby"),
    "lua" to listOf("lua"), "pl" to listOf("perl"), "php" to listOf("php"), "go" to listOf("go", "run"),
)

/** How to run the file at [path] in Termux, or null when its extension has no runner. */
fun runSpec(path: String): RunSpec? {
    val cmd = runners[path.substringAfterLast('.', "").lowercase()] ?: return null
    return RunSpec(BIN + cmd[0], cmd.drop(1) + path, path.substringBeforeLast('/').ifEmpty { "/" })
}
