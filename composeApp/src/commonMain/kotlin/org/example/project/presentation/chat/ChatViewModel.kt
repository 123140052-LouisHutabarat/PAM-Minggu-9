package org.example.project.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.AiError
import org.example.project.data.model.ChatMessage
import org.example.project.data.model.ChatUiState
import org.example.project.data.model.NutritionUiState
import org.example.project.domain.repository.AiRepository

/**
 * ViewModel untuk layar Chat dan Nutrisi.
 *
 * Pola yang digunakan:
 * - StateFlow untuk reactive UI (Compose mengobservasi perubahan state)
 * - viewModelScope untuk coroutine yang auto-cancel saat ViewModel destroyed
 * - update{} untuk atomic state update (thread-safe)
 */
class ChatViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    // ── Chat State ──────────────────────────────────────────────────────────
    private val _chatState = MutableStateFlow(ChatUiState())
    val chatState: StateFlow<ChatUiState> = _chatState.asStateFlow()

    // ── Nutrition State ─────────────────────────────────────────────────────
    private val _nutritionState = MutableStateFlow(NutritionUiState())
    val nutritionState: StateFlow<NutritionUiState> = _nutritionState.asStateFlow()

    // ── Chat Actions ────────────────────────────────────────────────────────

    /**
     * Kirim pesan ke AI.
     * 1. Tambah pesan user ke UI langsung (optimistic update)
     * 2. Kirim ke API (suspend)
     * 3. Tambah respons AI ke UI
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val userMsg = ChatMessage(text = message.trim(), isUser = true)
        _chatState.update { it.copy(
            messages  = it.messages + userMsg,
            isLoading = true,
            error     = null,
            inputText = ""
        )}

        viewModelScope.launch {
            aiRepository.chat(message.trim())
                .onSuccess { response ->
                    val aiMsg = ChatMessage(text = response, isUser = false)
                    _chatState.update { it.copy(
                        messages  = it.messages + aiMsg,
                        isLoading = false
                    )}
                }
                .onFailure { error ->
                    _chatState.update { it.copy(
                        isLoading = false,
                        error     = formatError(error)
                    )}
                }
        }
    }

    fun updateInput(text: String) = _chatState.update { it.copy(inputText = text) }

    fun clearChat() {
        aiRepository.clearChatHistory()
        _chatState.value = ChatUiState()
    }

    fun clearError() = _chatState.update { it.copy(error = null) }

    // ── Nutrition Actions ───────────────────────────────────────────────────

    fun analyzeFood(foodName: String) {
        if (foodName.isBlank()) return

        _nutritionState.update { it.copy(isLoading = true, error = null, result = null) }

        viewModelScope.launch {
            aiRepository.analyzeFood(foodName.trim())
                .onSuccess { info ->
                    _nutritionState.update { it.copy(result = info, isLoading = false) }
                }
                .onFailure { error ->
                    _nutritionState.update { it.copy(isLoading = false, error = formatError(error)) }
                }
        }
    }

    fun clearNutritionResult() { _nutritionState.value = NutritionUiState() }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private fun formatError(error: Throwable): String = when (error) {
        is AiError.Unauthorized   -> "❌ ${error.message}"
        is AiError.RateLimited    -> "⏳ ${error.message}"
        is AiError.NetworkError   -> "📡 ${error.message}"
        is AiError.ApiKeyMissing  -> "🔑 ${error.message}"
        is AiError.ServerError    -> "🔧 ${error.message}"
        is AiError.ParseError     -> "⚠️ ${error.message}"
        else                      -> "❓ ${error.message}"
    }
}
