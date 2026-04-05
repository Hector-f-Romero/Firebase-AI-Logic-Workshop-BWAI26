package com.hector.firebasebwai26.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val counter: Int = 0,
    val geminiMessage: String? = ""
)

class HomeViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            "gemini-2.5-flash-lite",
            systemInstruction = content {
                text("Interactuarás con personas que viven en la ciudad de Cali, Colombia. Considera las palabras coloquiales y lugares famosos de la ciudad cuando el usuario te hable.")
            }
        )

    private val chat = model.startChat()

    fun chatWithGemini(prompt: String) {
        viewModelScope.launch {
            val rawResponse = chat.sendMessage(prompt)

            _uiState.update { it.copy(geminiMessage = rawResponse.text) }
        }
    }

    fun increaseCounter() {
        val newValue = _uiState.value.counter + 1

        _uiState.update { it.copy(counter = newValue) }
    }
}
