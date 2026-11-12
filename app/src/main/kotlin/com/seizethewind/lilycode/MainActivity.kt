package com.seizethewind.lilycode

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.seizethewind.lilycode.ui.App
import com.seizethewind.lilycode.ui.theme.LilyTheme

class MainActivity : ComponentActivity() {
    private val incoming = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incoming.value = intent.getStringExtra(EXTRA_PATH)
        setContent { LilyTheme { App(incoming) } }
    }

    // singleTask: `lily <file>` while the app is open arrives here
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        incoming.value = intent.getStringExtra(EXTRA_PATH)
    }

    companion object { const val EXTRA_PATH = "path" }
}
