package com.hector.firebasebwai26.presentation.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
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

open class HomeViewModel() : ViewModel() {
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

    private fun generateBitmap(uri: Uri, context: Context): Bitmap {
        val bitmap =
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))

        return bitmap
    }

    fun chatWithGemini(prompt: String, imageUri: Uri?, context: Context) {
        viewModelScope.launch {

            var geminiResponse: String?

            if (imageUri == null) {
                val rawResponse = chat.sendMessage(prompt)

                geminiResponse = rawResponse.text
            } else {

                val bitmap = generateBitmap(imageUri, context)

                val rawResponse = chat.sendMessage(content {
                    text(prompt)
                    image(bitmap)
                })

                geminiResponse = rawResponse.text
            }

            _uiState.update { it.copy(geminiMessage = geminiResponse) }
        }
    }
}
