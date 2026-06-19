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
    onNavigateToExercise: () -> Unit,
    onBack: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onViewHistory: (() -> Unit)? = null,
    userName: String? = null,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.shouldNavigateToExercise) {
        if (state.shouldNavigateToExercise) {
            onNavigateToExercise()
            viewModel.onNavigationHandled()
        }
    }

    HeartRateInputContent(
        title = stringResource(R.string.measure_current_heart_rate),
        subtitle = stringResource(R.string.measure_to_see_effect),
        buttonText = stringResource(R.string.start_exercise),
        infoText = stringResource(R.string.panic_heart_rate_hint),
        onExerciseButtonClick = viewModel::onBeginExercises,
        onAddBpmButtonClick = { viewModel.addBpm(bpm = it, isBefore = true) },
        onBack = onBack,
        onLogout = onLogout,
        onViewHistory = onViewHistory,
        userName = userName,
    )
}