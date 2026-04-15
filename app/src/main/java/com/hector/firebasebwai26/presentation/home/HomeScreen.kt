package com.hector.firebasebwai26.presentation.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.hector.firebasebwai26.R


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var userPrompt by remember { mutableStateOf("") }

    // Guardamos la URI de referencia a la imagen que queremos cargar.
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Encargada de lanzar el contrato para seleccionar una foto desde nuestra galería por defecto.
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedImageUri = uri
        }

    val context = LocalContext.current

    Column(modifier = modifier) {


        Text(
            "Bienvenidos al Workshop GDG 2026",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFD7D6D6))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (uiState.isLoading) {
                Spacer(Modifier.weight(1f))

                CircularProgressIndicator(
                    Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.weight(1f))
                return@Column
            }

            if (!uiState.errorMessage.isBlank()) {
                Text("ERROR: ${uiState.errorMessage}", color = Color.Red)
            } else {
                Text("Respuesta de Gemini: ${uiState.geminiMessage}")

                uiState.geminiImage?.let { bitmap ->
                    AsyncImage(model = bitmap, contentDescription = "", modifier.size(400.dp))
                }
            }
        }

        Column(
            Modifier.fillMaxWidth()
        ) {

            selectedImageUri?.let { uri ->
                Box(Modifier
                    .size(130.dp)
                    .padding(8.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Image prompt",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = {
                            selectedImageUri = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp)
                            .background(
                                shape = CircleShape,
                                color = Color.White
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_cancel),
                            contentDescription = "Delete selected photo",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

            }

            TextField(
                value = userPrompt,
                onValueChange = { newValue -> userPrompt = newValue },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth()
            ) {

                Button(onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Agregar foto", maxLines = 2)
                }

                Spacer(Modifier.weight(1f))

                Button(onClick = {
                    homeViewModel.chatWithGemini(
                        prompt = userPrompt,
                        imageUri = selectedImageUri,
                        context = context,
                    )
                }) {

                    Text("Preguntarle a Gemini", maxLines = 2)
                }
            }
        }
    }
}