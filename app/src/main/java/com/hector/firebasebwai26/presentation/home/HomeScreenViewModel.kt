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
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val counter: Int = 0,
    val geminiMessage: String? = "",
    val geminiImage: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

open class HomeViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val model = Firebase.ai(backend = GenerativeBackend.vertexAI())
        .generativeModel(
            "gemini-2.5-flash-image",
            systemInstruction = content {
                text("Interactuarás con personas que viven en la ciudad de Cali, Colombia. Crearás de forma iteractiva historias de acuerdo a la temática que te soliciten buscando que cada respuesta que generes no supere las 200 palabras. Considera las palabras coloquiales y lugares famosos de la ciudad para crear las narrativas. Cuando debas generar imágenes, siempre usa un estilo cartoon.")
            },
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
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
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Guardamos la respuesta cruda de Gemini para chat de solo texto y con carga de imágenes
                val rawResponse = if (imageUri == null) {
                    chat.sendMessage(prompt)
                } else {
                    val bitmap = generateBitmap(imageUri, context)

                    chat.sendMessage(content {
                        text(prompt)
                        image(bitmap)
                    })
                }

                // Por cómo está construido Gemini, puede retornar respuestas alternativas llamadas Candidates.
                // Candidate = secuencia de tokens para construir una respuesta.
                val parts = rawResponse.candidates.first().content.parts

                parts.forEach { part ->
                    when (part) {
                        is ImagePart -> {
                            // Tratamos de convertir los bytes de la respuesta a un bitmap.
                            val generatedBitmap = part.asImageOrNull()
                            _uiState.update { it.copy(geminiImage = generatedBitmap) }
                        }

                        is TextPart -> {
                            _uiState.update { it.copy(geminiMessage = part.text) }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Error al usar el servicio de Gemini") }
            }finally {
                _uiState.update { it.copy(isLoading = false) }
            }

        }
    }
}
