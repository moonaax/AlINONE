package com.allinone.music.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.allinone.music.data.api.HttpClientFactory
import com.allinone.music.data.api.KuwoApi
import com.allinone.music.data.repository.MusicRepository
import com.allinone.music.ui.home.HomeScreen
import com.allinone.music.ui.search.SearchScreen

@Composable
fun MainScreen(onBack: () -> Unit = {}) {
    val repository = remember {
        val client = HttpClientFactory.create()
        MusicRepository(KuwoApi(client))
    }

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "推荐") },
                    label = { Text("推荐") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                    label = { Text("搜索") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> HomeScreen(repository = repository, onBack = onBack)
                1 -> SearchScreen(repository = repository)
            }
        }
    }
}
