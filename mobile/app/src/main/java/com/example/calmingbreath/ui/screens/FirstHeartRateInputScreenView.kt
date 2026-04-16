package com.example.calmingbreath.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.example.calmingbreath.R
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel

@Composable
fun FirstHeartRateInputScreenView(
    viewModel: HeartRateInputViewModel,
    onNavigateToExercise: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.shouldNavigateToExercise) {
        if (state.shouldNavigateToExercise) {
            onNavigateToExercise()
            viewModel.onNavigationHandled()
        }
    }

    HeartRateInputContent(
        title = stringResource(R.string.input_your_current_heart_rate),
        subtitle = stringResource(R.string.measure_your_bpm),
        buttonText = stringResource(R.string.begin_exercises),
        onExerciseButtonClick = viewModel::onBeginExercises,
        onAddBpmButtonClick = { viewModel.addBpm(bpm = it, isBefore = true) }
    )
}