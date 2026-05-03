package org.example.project.data.model

/**
 * Model pesan untuk tampilan UI chat.
 */
data class ChatMessage(
    val id: String = generateId(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = currentTimeMillis()
)

/** State keseluruhan layar chat */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

/** State untuk analisis nutrisi */
data class NutritionUiState(
    val result: NutritionInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Platform-specific utility functions (expect/actual)
expect fun generateId(): String
expect fun currentTimeMillis(): Long
