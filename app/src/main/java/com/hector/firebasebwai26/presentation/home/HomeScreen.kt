package com.hector.firebasebwai26.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = viewModel()) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        Text("Bienvenidos al Workshop GDG 2026")

        Text(text = uiState.counter.toString())

        Button(onClick = {
            homeViewModel.increaseCounter()
        }) {
            Text("Aumentar contador")
        }
    }
}