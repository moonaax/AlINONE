package com.allinone.music.data.api

import com.allinone.music.data.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

class KuwoApi(private val client: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun searchSongs(keyword: String, page: Int = 1, pageSize: Int = 20): List<Song> {
        val response = client.get("https://search.kuwo.cn/r.s") {
            parameter("all", keyword)
            parameter("ft", "music")
            parameter("client", "kt")
            parameter("pn", (page - 1).toString())
            parameter("rn", pageSize.toString())
            parameter("rformat", "json")
            parameter("encoding", "utf8")
            parameter("vipver", "1")
            parameter("ver", "kwplayer_ar_12.2.2.0")
            parameter("strategy", "2012")
            parameter("vermerge", "1")
            parameter("mobi", "1")
            parameter("issubtitle", "1")
            parameter("cluster", "0")
        }
        val text = response.bodyAsText()
        return parseSearchSongs(text)
    }

    suspend fun getSongUrl(rid: String): String {
        val response = client.get("https://antiserver.kuwo.cn/anti.s") {
            parameter("type", "convert_url")
            parameter("rid", "MUSIC_$rid")
            parameter("format", "mp3")
            parameter("response", "url")
        }
        val text = response.bodyAsText().trim()
        return if (text.startsWith("http")) text else ""
    }

    suspend fun getRecommendPlaylists(page: Int = 1, pageSize: Int = 30): List<Playlist> {
        val response = client.get("http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList") {
            parameter("pn", page.toString())
            parameter("rn", pageSize.toString())
            parameter("order", "hot")
            headers { append("Referer", "http://www.kuwo.cn/") }
        }
        val text = response.bodyAsText()
        return parseRecommendPlaylists(text)
    }

    suspend fun getCategoryPlaylists(tagId: Int, page: Int = 1, pageSize: Int = 30): List<Playlist> {
        val response = client.get("http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList") {
            parameter("id", tagId.toString())
            parameter("pn", page.toString())
            parameter("rn", pageSize.toString())
            headers { append("Referer", "http://www.kuwo.cn/") }
        }
        val text = response.bodyAsText()
        return parseRecommendPlaylists(text) // Same response format
    }

    suspend fun getPlaylistInfo(pid: String): Playlist {
        val response = client.get("http://nplserver.kuwo.cn/pl.svc") {
            parameter("op", "getlistinfo")
            parameter("pid", pid)
            parameter("pn", "0")
            parameter("rn", "100")
            parameter("encode", "utf8")
            parameter("keyset", "pl2012")
        }
        val text = response.bodyAsText()
        return parsePlaylistInfo(text, pid)
    }

    // --- Parsers ---

    private fun parseSearchSongs(text: String): List<Song> {
        val jsonText = fixKuwoJson(text)
        return try {
            val obj = json.parseToJsonElement(jsonText).jsonObject
            val abslist = obj["abslist"]?.jsonArray ?: return emptyList()
            abslist.mapNotNull { element ->
                val item = element.jsonObject
                val rid = item["MUSICRID"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val cleanRid = rid.removePrefix("MUSIC_")
                val name = (item["SONGNAME"]?.jsonPrimitive?.content ?: "").replace("&nbsp;", " ")
                val artist = (item["ARTIST"]?.jsonPrimitive?.content ?: "").replace("&nbsp;", " ")
                val album = (item["ALBUM"]?.jsonPrimitive?.content ?: "").replace("&nbsp;", " ")
                val albumId = item["ALBUMID"]?.jsonPrimitive?.content ?: ""
                val duration = item["DURATION"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                val cover = if (albumId.isNotEmpty() && albumId != "0")
                    "https://img4.kuwo.cn/star/albumcover/300/$albumId.jpg" else ""
                Song(
                    id = cleanRid, name = name, artist = artist,
                    album = album, albumId = albumId, albumCover = cover,
                    duration = duration, source = MusicSource.KUWO,
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseRecommendPlaylists(text: String): List<Playlist> {
        return try {
            val obj = json.parseToJsonElement(text).jsonObject
            val dataObj = obj["data"]?.jsonObject ?: return emptyList()
            val list = dataObj["data"]?.jsonArray ?: return emptyList()
            list.mapNotNull { element ->
                val item = element.jsonObject
                val id = item["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val name = item["name"]?.jsonPrimitive?.content ?: ""
                val img = item["img"]?.jsonPrimitive?.content ?: ""
                val listenCnt = item["listencnt"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0
                val total = item["total"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                Playlist(id = id, name = name, cover = img, playCount = listenCnt, songCount = total)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parsePlaylistInfo(text: String, pid: String): Playlist {
        return try {
            val obj = json.parseToJsonElement(text).jsonObject
            val title = obj["title"]?.jsonPrimitive?.content ?: ""
            val pic = obj["pic"]?.jsonPrimitive?.content ?: ""
            val info = obj["info"]?.jsonPrimitive?.content ?: ""
            val total = obj["total"]?.jsonPrimitive?.intOrNull ?: 0
            val playNum = obj["playnum"]?.jsonPrimitive?.longOrNull ?: 0
            Playlist(id = pid, name = title, cover = pic, description = info,
                playCount = playNum, songCount = total)
        } catch (e: Exception) {
            Playlist(id = pid, name = "")
        }
    }

    /** Fix Kuwo's non-standard single-quote JSON to valid JSON */
    private fun fixKuwoJson(text: String): String {
        val trimmed = text.trim()
        // If it's already valid JSON (starts with { and can be parsed), return as-is
        if (trimmed.startsWith("{")) {
            // Try parsing directly first
            try {
                Json { isLenient = true }.parseToJsonElement(trimmed)
                return trimmed.replace("&nbsp;", " ")
            } catch (_: Exception) { }
        }
        // Convert single quotes to double quotes
        val sb = StringBuilder()
        var inSingle = false
        var inDouble = false
        var prev = '\u0000'
        for (c in trimmed) {
            when {
                c == '\'' && !inDouble && prev != '\\' -> {
                    inSingle = !inSingle
                    sb.append('"')
                }
                c == '"' && !inSingle && prev != '\\' -> {
                    inDouble = !inDouble
                    sb.append(c)
                }
                else -> sb.append(c)
            }
            prev = c
        }
        return sb.toString().replace("&nbsp;", " ")
    }

    companion object {
        val CATEGORY_TAGS = mapOf(
            "推荐" to 0,
            "伤感" to 146, "放松" to 153, "开心" to 147, "安静" to 152, "甜蜜" to 241,
            "华语" to 37, "欧美" to 38, "日韩" to 39, "粤语" to 46,
            "流行" to 236, "摇滚" to 11, "民谣" to 37, "电子" to 237,
            "轻音乐" to 13, "说唱" to 247, "DJ" to 1, "古典" to 243,
            "经典" to 211, "网络" to 87, "翻唱" to 244, "国风" to 12,
        )
    }
}
