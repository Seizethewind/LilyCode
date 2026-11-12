package com.seizethewind.lilycode.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.seizethewind.lilycode.util.RunSpec
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class ExecResult(val exit: Int, val text: String)

/** Sends commands to Termux's RunCommandService; results return to [RunResultReceiver]. */
object TermuxRun {
    const val PERM = "com.termux.permission.RUN_COMMAND"
    private val waiting = ConcurrentHashMap<Int, CompletableDeferred<ExecResult>>()
    private val ids = AtomicInteger(1)

    fun allowed(ctx: Context) = ctx.checkSelfPermission(PERM) == PackageManager.PERMISSION_GRANTED

    /** Id 0 sends the result to the output panel; any other id goes to the [exec] waiting for it. */
    fun start(ctx: Context, spec: RunSpec, id: Int = 0) {
        // Termux fills the result into the intent, so the PendingIntent must be mutable on API 31+.
        // The id is the request code, so each command gets its own PendingIntent.
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        val back = Intent(ctx, RunResultReceiver::class.java).putExtra("id", id)
        val i = Intent("com.termux.RUN_COMMAND")
            .setClassName("com.termux", "com.termux.app.RunCommandService")
            .putExtra("com.termux.RUN_COMMAND_PATH", spec.exe)
            .putExtra("com.termux.RUN_COMMAND_ARGUMENTS", spec.args.toTypedArray())
            .putExtra("com.termux.RUN_COMMAND_WORKDIR", spec.dir)
            .putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
            .putExtra("com.termux.RUN_COMMAND_PENDING_INTENT", PendingIntent.getBroadcast(ctx, id, back, flags))
        ctx.startService(i)
    }

    /** Runs [spec] in Termux and waits for it to end. */
    suspend fun exec(ctx: Context, spec: RunSpec): ExecResult {
        val id = ids.getAndIncrement()
        val waiter = CompletableDeferred<ExecResult>()
        waiting[id] = waiter
        try {
            start(ctx, spec, id)
            return withTimeoutOrNull(15_000) { waiter.await() }
                ?: error("Termux did not answer. Check allow-external-apps and the Run permission.")
        } finally {
            waiting.remove(id)
        }
    }

    /** True if an [exec] was waiting for [id]. */
    fun deliver(id: Int, r: ExecResult) = waiting.remove(id)?.complete(r) != null
}
