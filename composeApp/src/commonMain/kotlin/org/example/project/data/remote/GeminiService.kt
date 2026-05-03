package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.example.project.config.ApiConfig
import org.example.project.data.model.AiError
import org.example.project.data.model.GeminiContent
import org.example.project.data.model.GeminiGenerationConfig
import org.example.project.data.model.GeminiPart
import org.example.project.data.model.GeminiRequest
import org.example.project.data.model.GeminiResponse
import org.example.project.data.model.NutritionInfo

/**
 * Service untuk berinteraksi dengan Google Gemini API.
 *
 * Fitur:
 * - Single-turn: generateContent() — satu pertanyaan, satu jawaban
 * - Multi-turn:  chat() — percakapan dengan riwayat (context window)
 * - Structured:  analyzeFood() — parse JSON dari respons AI
 */
class GeminiService(private val client: HttpClient) {

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model   = "gemini-2.5-flash"

    // Riwayat percakapan untuk multi-turn
    private val conversationHistory = mutableListOf<GeminiContent>()

    // ── Single-turn ────────────────────────────────────────────────────────

    /**
     * Kirim satu prompt dan dapatkan satu respons.
     * Tidak menyimpan riwayat.
     *
     * @param prompt         Pertanyaan atau instruksi
     * @param systemPrompt   System instruction opsional
     */
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = safeApiCall {

        validateApiKey()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user")
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 1000
            ),
            systemInstruction = systemPrompt?.let {
                GeminiContent(parts = listOf(GeminiPart(text = it)), role = "user")
            }
        )

        val response: GeminiResponse = client.post(
            "$baseUrl/models/$model:generateContent"
        ) {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()

        response.error?.let { err ->
            throw when (err.code) {
                401, 403 -> AiError.Unauthorized(err.message)
                429      -> AiError.RateLimited(message = err.message)
                else     -> AiError.ServerError(err.message)
            }
        }

        response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw AiError.ParseError("Respons AI kosong")
    }

    // ── Multi-turn ─────────────────────────────────────────────────────────

    /**
     * Kirim pesan dalam percakapan dengan riwayat.
     * Riwayat otomatis disimpan agar AI "ingat" konteks.
     *
     * @param userMessage  Pesan dari user
     * @param systemPrompt System instruction (hanya dipakai di pesan pertama)
     */
    suspend fun chat(
        userMessage: String,
        systemPrompt: String? = null
    ): Result<String> = safeApiCall {

        validateApiKey()

        // Tambah pesan user ke riwayat
        conversationHistory.add(
            GeminiContent(parts = listOf(GeminiPart(text = userMessage)), role = "user")
        )

        val request = GeminiRequest(
            contents = conversationHistory.toList(),
            generationConfig = GeminiGenerationConfig(temperature = 0.7, maxOutputTokens = 1000),
            systemInstruction = if (conversationHistory.size == 1 && systemPrompt != null)
                GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)), role = "user")
            else null
        )

        val response: GeminiResponse = client.post(
            "$baseUrl/models/$model:generateContent"
        ) {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()

        val assistantText = response.candidates.firstOrNull()
            ?.content?.parts?.firstOrNull()?.text
            ?: throw AiError.ParseError("Respons AI kosong")

        // Tambah respons AI ke riwayat
        conversationHistory.add(
            GeminiContent(parts = listOf(GeminiPart(text = assistantText)), role = "model")
        )

        assistantText
    }

    // ── Structured Output ──────────────────────────────────────────────────

    /**
     * Analisis nutrisi makanan → parse ke NutritionInfo (data class).
     * Contoh Prompt Engineering: memaksa output JSON terstruktur.
     */
    suspend fun analyzeFood(foodName: String): Result<NutritionInfo> {
        val prompt = """
            Analisis informasi gizi untuk: $foodName
            PENTING: Balas HANYA dengan JSON valid. Jangan tambahkan teks atau markdown apapun.
        """.trimIndent()

        return generateContent(prompt, SystemPrompts.NUTRITIONIST)
            .mapCatching { raw ->
                // Bersihkan dari markdown jika AI tetap menambahkannya
                val clean = raw
                    .removePrefix("```json").removePrefix("```")
                    .removeSuffix("```").trim()
                try {
                    Json { ignoreUnknownKeys = true }.decodeFromString<NutritionInfo>(clean)
                } catch (e: SerializationException) {
                    throw AiError.ParseError("Gagal parse data nutrisi: ${e.message}")
                }
            }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    fun clearHistory() { conversationHistory.clear() }
    val historySize: Int get() = conversationHistory.size

    private fun validateApiKey() {
        if (ApiConfig.geminiApiKey.isBlank())
            throw AiError.ApiKeyMissing("Gemini")
    }
}

// ── safeApiCall: centralized error handling ────────────────────────────────

/**
 * Wrapper coroutine yang mengkonversi semua exception ke AiError.
 * Dipanggil di setiap fungsi GeminiService agar error handling terpusat.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: AiError) {
        Result.failure(e)
    } catch (e: ClientRequestException) {
        Result.failure(when (e.response.status.value) {
            401, 403 -> AiError.Unauthorized()
            429      -> AiError.RateLimited()
            in 400..499 -> AiError.Unknown("Request error: ${e.message}")
            else        -> AiError.ServerError()
        })
    } catch (e: ServerResponseException) {
        Result.failure(AiError.ServerError("Server ${e.response.status.value}"))
    } catch (e: IOException) {
        Result.failure(AiError.NetworkError("Network: ${e.message}"))
    } catch (e: SerializationException) {
        Result.failure(AiError.ParseError("Parse: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(AiError.Unknown(e.message ?: "Unknown error"))
    }
}
