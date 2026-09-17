package com.allinone.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.allinone.music.ui.LandingScreen
import com.allinone.music.ui.MainScreen
import com.allinone.music.ui.uikit.UIKitScreen

enum class AppRoute { Landing, Music, UIKit }

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
    }

    var route by remember { mutableStateOf(AppRoute.Landing) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (route) {
                AppRoute.Landing -> LandingScreen(
                    onOpenMusic = { route = AppRoute.Music },
                    onOpenUIKit = { route = AppRoute.UIKit },
                )
                AppRoute.Music -> MainScreen(onBack = { route = AppRoute.Landing })
                AppRoute.UIKit -> UIKitScreen(onBack = { route = AppRoute.Landing })
            }
        }
    }
}
