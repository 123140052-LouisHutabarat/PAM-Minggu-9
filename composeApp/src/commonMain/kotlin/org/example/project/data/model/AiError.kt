package org.example.project.data.model

/**
 * Sealed class untuk semua jenis error dari AI API.
 * Dengan sealed class, compiler memastikan semua kasus ditangani di when().
 */
sealed class AiError(message: String) : Exception(message) {
    data class Unauthorized(
        override val message: String = "API key tidak valid. Periksa konfigurasi di local.properties."
    ) : AiError(message)

    data class RateLimited(
        val retryAfterSeconds: Int = 60,
        override val message: String = "Terlalu banyak request. Coba lagi dalam beberapa detik."
    ) : AiError(message)

    data class ServerError(
        override val message: String = "Server AI sedang bermasalah. Coba lagi nanti."
    ) : AiError(message)

    data class NetworkError(
        override val message: String = "Tidak ada koneksi internet. Periksa koneksi Anda."
    ) : AiError(message)

    data class ParseError(
        override val message: String = "Gagal memproses respons AI."
    ) : AiError(message)

    data class ApiKeyMissing(
        val provider: String = "AI"
    ) : AiError("API key $provider belum diisi. Tambahkan GEMINI_API_KEY ke local.properties.")

    data class Unknown(
        override val message: String = "Terjadi kesalahan tidak diketahui."
    ) : AiError(message)
}
