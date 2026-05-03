package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Gemini Request ──────────────────────────────────────────

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerialName("generationConfig")
    val generationConfig: GeminiGenerationConfig? = null,
    @SerialName("systemInstruction")
    val systemInstruction: GeminiContent? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    @SerialName("maxOutputTokens") val maxOutputTokens: Int = 1000,
    @SerialName("topP") val topP: Double = 0.95
)

// ── Gemini Response ─────────────────────────────────────────

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    @SerialName("finishReason") val finishReason: String? = null
)

@Serializable
data class GeminiError(
    val code: Int = 0,
    val message: String = "",
    val status: String = ""
)

// ── Nutrition (Structured Output) ───────────────────────────

@Serializable
data class NutritionInfo(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    @SerialName("healthTips") val healthTips: List<String> = emptyList()
)
