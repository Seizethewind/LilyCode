package com.seizethewind.lilycode.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.seizethewind.lilycode.util.RunSpec

/** Sends a command to Termux's RunCommandService; the result returns to [RunResultReceiver]. */
object TermuxRun {
    const val PERM = "com.termux.permission.RUN_COMMAND"

    fun allowed(ctx: Context) = ctx.checkSelfPermission(PERM) == PackageManager.PERMISSION_GRANTED

    fun start(ctx: Context, spec: RunSpec) {
        // Termux fills the result into the intent, so the PendingIntent must be mutable on API 31+
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        val result = PendingIntent.getBroadcast(ctx, 0, Intent(ctx, RunResultReceiver::class.java), flags)
        val i = Intent("com.termux.RUN_COMMAND")
            .setClassName("com.termux", "com.termux.app.RunCommandService")
            .putExtra("com.termux.RUN_COMMAND_PATH", spec.exe)
            .putExtra("com.termux.RUN_COMMAND_ARGUMENTS", spec.args.toTypedArray())
            .putExtra("com.termux.RUN_COMMAND_WORKDIR", spec.dir)
            .putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
            .putExtra("com.termux.RUN_COMMAND_PENDING_INTENT", result)
        ctx.startService(i)
    }
}
