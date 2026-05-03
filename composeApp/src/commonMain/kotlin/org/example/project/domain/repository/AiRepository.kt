package org.example.project.domain.repository

import org.example.project.data.model.NutritionInfo
import org.example.project.data.remote.GeminiService
import org.example.project.data.remote.SystemPrompts

/**
 * Repository interface — abstraksi antara domain dan data layer.
 *
 * Keuntungan pattern ini:
 * - ViewModel tidak tahu "Gemini" atau "OpenAI" — hanya tahu AiRepository
 * - Mudah ganti provider AI tanpa ubah ViewModel/UI
 * - Mudah dites dengan mock implementation
 */
interface AiRepository {
    suspend fun chat(message: String): Result<String>
    suspend fun analyzeFood(foodName: String): Result<NutritionInfo>
    suspend fun summarize(text: String): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    fun clearChatHistory()
}

/**
 * Implementasi menggunakan Gemini API.
 */
class AiRepositoryImpl(
    private val geminiService: GeminiService
) : AiRepository {

    override suspend fun chat(message: String): Result<String> =
        geminiService.chat(
            userMessage  = message,
            systemPrompt = SystemPrompts.ITERA_ASSISTANT
        )

    override suspend fun analyzeFood(foodName: String): Result<NutritionInfo> =
        geminiService.analyzeFood(foodName)

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut dalam 2-3 kalimat padat dan informatif.
            Fokus pada poin-poin utama. Gunakan Bahasa Indonesia.
            
            Teks:
            $text
        """.trimIndent()
        return geminiService.generateContent(prompt)
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan teks berikut ke dalam bahasa $targetLanguage.
            Terjemahkan secara natural dan kontekstual.
            
            Teks:
            $text
        """.trimIndent()
        return geminiService.generateContent(prompt, SystemPrompts.TRANSLATOR)
    }

    override fun clearChatHistory() = geminiService.clearHistory()
}
