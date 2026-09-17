package com.allinone.music.data.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }
        defaultRequest {
            headers {
                append(HttpHeaders.UserAgent, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
            }
        }
    }
}
