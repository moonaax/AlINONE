package com.allinone.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.allinone.music.ui.MainScreen

@Composable
fun App() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen()
        }
    }
}
