package com.seizethewind.lilycode.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Receives the PendingIntent Termux fires when a background command ends. */
class RunResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val r = intent.getBundleExtra("result")
        val parts = listOf(r?.getString("stdout"), r?.getString("stderr"), r?.getString("errmsg"))
        val text = parts.filter { !it.isNullOrEmpty() }.joinToString("\n")
            .ifEmpty { if (r == null) "No result from Termux." else "" }
        val exit = r?.getInt("exitCode", -1) ?: -1
        if (!TermuxRun.deliver(intent.getIntExtra("id", 0), ExecResult(exit, text))) {
            RunOutput.finish("$text\n[exit $exit]".trim())
        }
    }
}
