package com.hector.firebasebwai26.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var userPrompt by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text("Bienvenidos al Workshop GDG 2026", fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))

        Column(Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color(0xFFD7D6D6))
            .padding(20.dp)) {
            Text("Respuesta de Gemini: ${uiState.geminiMessage}")
        }

        Column(
            Modifier
                .fillMaxWidth()
        ) {

            TextField(
                value = userPrompt,
                onValueChange = { newValue -> userPrompt = newValue },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier
                    .fillMaxWidth()
            ) {

                Button(onClick = {
                    // TODO:
                }) {
                    Text("Agregar foto", maxLines = 2)
                }

                Spacer(Modifier.weight(1f))

                Button(onClick = {
                    homeViewModel.chatWithGemini(userPrompt)
                }) {

                    Text("Preguntarle a Gemini", maxLines = 2)
                }
            }
        }
    }
}