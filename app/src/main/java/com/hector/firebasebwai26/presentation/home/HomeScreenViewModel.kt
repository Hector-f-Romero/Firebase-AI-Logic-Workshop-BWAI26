package com.hector.firebasebwai26.presentation.home

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val counter: Int = 0
)

class HomeViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel("gemini-2.5-flash-lite")

    fun increaseCounter() {
        val newValue = _uiState.value.counter + 1

        _uiState.update { it.copy(counter = newValue) }
    }
}