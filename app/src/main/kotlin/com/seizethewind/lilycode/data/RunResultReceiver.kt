package com.seizethewind.lilycode.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Receives the PendingIntent Termux fires when a background command ends. */
class RunResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val r = intent.getBundleExtra("result") ?: return RunOutput.finish("No result from Termux.")
        val parts = listOf(r.getString("stdout"), r.getString("stderr"), r.getString("errmsg"))
        val text = parts.filter { !it.isNullOrEmpty() }.joinToString("\n")
        RunOutput.finish("$text\n[exit ${r.getInt("exitCode")}]".trim())
    }
}
