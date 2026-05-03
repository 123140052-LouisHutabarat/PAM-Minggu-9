package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory untuk membuat HttpClient Ktor yang sudah terkonfigurasi.
 * Gunakan satu instance di seluruh aplikasi (singleton via remember{}).
 */
fun createHttpClient(): HttpClient = HttpClient {

    // Plugin JSON serialization otomatis
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true  // abaikan field baru dari API
            isLenient = true
            encodeDefaults = true
        })
    }

    // Logging untuk debugging (lihat di Logcat / console)
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("[KTOR] $message")
            }
        }
        level = LogLevel.HEADERS // ganti ke LogLevel.BODY untuk debug JSON penuh
    }

    // Timeout agar app tidak hang
    install(HttpTimeout) {
        requestTimeoutMillis  = 30_000
        connectTimeoutMillis  = 15_000
        socketTimeoutMillis   = 30_000
    }
}
