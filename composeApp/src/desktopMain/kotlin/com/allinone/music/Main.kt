package com.allinone.music

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ALLINONE Music",
        state = rememberWindowState(width = 1100.dp, height = 750.dp),
    ) {
        App()
    }
}
