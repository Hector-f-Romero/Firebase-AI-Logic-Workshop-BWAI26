package com.hector.firebasebwai26.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = viewModel()) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var userPrompt by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text("Bienvenidos al Workshop GDG 2026")

        TextField(value = userPrompt, onValueChange = { newValue ->
            userPrompt = newValue
        })

        Button(onClick = {
            homeViewModel.chatWithGemini(userPrompt)
        }) {
            Text("Preguntarle a Gemini")
        }

        Text("Respuesta de Gemini: ${uiState.geminiMessage}")
    }
}