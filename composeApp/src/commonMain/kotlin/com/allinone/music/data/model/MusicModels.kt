package com.allinone.music.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val name: String,
    val artist: String,
    val album: String = "",
    val albumId: String = "",
    val albumCover: String = "",
    val duration: Int = 0, // seconds
    val url: String = "",
    val source: MusicSource = MusicSource.KUWO,
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val cover: String = "",
    val description: String = "",
    val playCount: Long = 0,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList(),
)

@Serializable
data class SearchResult(
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val hasMore: Boolean = false,
)

enum class MusicSource(val displayName: String) {
    KUWO("酷我音乐"),
    KUGOU("酷狗音乐"),
}
