package com.example.calmingbreath.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.calmingbreath.R
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel

@Composable
fun SecondHeartRateInputScreenView(
    viewModel: HeartRateInputViewModel,
    onNavigateToResults: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.shouldNavigateToResults) {
        if (state.shouldNavigateToResults) {
            onNavigateToResults()
            viewModel.onNavigationHandled()
        }
    }

    HeartRateInputContent(
        title = stringResource(R.string.exercises_completed),
        subtitle = stringResource(R.string.measure_your_bpm_after),
        buttonText = stringResource(R.string.see_results),
        onExerciseButtonClick = viewModel::onSeeResults,
        onAddBpmButtonClick = {
            viewModel.addBpm(bpm = it, isBefore = false)
            viewModel.updateExerciseNote()
        }
    )
}
