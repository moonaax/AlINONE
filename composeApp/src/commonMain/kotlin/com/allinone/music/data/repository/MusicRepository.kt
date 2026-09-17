package com.allinone.music.data.repository

import com.allinone.music.data.api.KuwoApi
import com.allinone.music.data.model.*

class MusicRepository(private val kuwoApi: KuwoApi) {

    suspend fun search(keyword: String, page: Int = 1): List<Song> {
        return kuwoApi.searchSongs(keyword, page)
    }

    suspend fun getSongUrl(song: Song): String {
        return kuwoApi.getSongUrl(song.id)
    }

    suspend fun getRecommendPlaylists(page: Int = 1): List<Playlist> {
        return kuwoApi.getRecommendPlaylists(page)
    }

    suspend fun getCategoryPlaylists(tagId: Int, page: Int = 1): List<Playlist> {
        return kuwoApi.getCategoryPlaylists(tagId, page)
    }

    suspend fun getPlaylistDetail(pid: String): Playlist {
        val info = kuwoApi.getPlaylistInfo(pid)
        // If playlist has no songs from API, search by playlist name/tags
        if (info.songs.isEmpty() && info.name.isNotEmpty()) {
            val songs = kuwoApi.searchSongs(info.name, pageSize = 30)
            return info.copy(songs = songs)
        }
        return info
    }
}
