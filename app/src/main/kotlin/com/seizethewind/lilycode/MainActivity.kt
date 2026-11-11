package com.seizethewind.lilycode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.seizethewind.lilycode.ui.EditorScreen
import com.seizethewind.lilycode.ui.HomeScreen
import com.seizethewind.lilycode.ui.LilyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LilyTheme {
                var editing by remember { mutableStateOf(false) }
                BackHandler(editing) { editing = false }
                if (editing) EditorScreen(onHome = { editing = false })
                else HomeScreen(onOpen = { editing = true })
            }
        }
    }
}
