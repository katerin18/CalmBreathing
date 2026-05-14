package com.example.calmingbreath.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.ui.viewmodel.BreathingPhase
import com.example.calmingbreath.ui.viewmodel.ExerciseViewModel

@Composable
fun ExerciseScreenView(
    viewModel: ExerciseViewModel,
    onNavigateNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val cycles = remember { viewModel.cycles }

    LaunchedEffect(state.shouldNavigateNext) {
        if (state.shouldNavigateNext) {
            onNavigateNext()
            viewModel.onNavigationHandled()
        }
    }

    // Анимация круга дыхания
    val scale = remember { Animatable(0.3f) }

    LaunchedEffect(state.currentCycleIndex, state.currentPhase, state.isPaused) {
        if (state.isPaused || state.isFinished) return@LaunchedEffect
        val cycle = cycles[state.currentCycleIndex]
        when (state.currentPhase) {
            BreathingPhase.INHALE -> {
                scale.snapTo(0.3f)
                scale.animateTo(1f, tween(cycle.inhaleSeconds * 1000))
            }
            BreathingPhase.EXHALE -> {
                scale.snapTo(1f)
                scale.animateTo(0.3f, tween(cycle.exhaleSeconds * 1000))
            }
            BreathingPhase.HOLD -> Unit
        }
    }

    val currentCycle = cycles[state.currentCycleIndex]
    val phaseText = when (state.currentPhase) {
        BreathingPhase.INHALE -> "Inhale"
        BreathingPhase.EXHALE -> "Exhale"
        BreathingPhase.HOLD -> "Hold"
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Calming Breathing",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Stage ${currentCycle.stage} of 5",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Inhale ${currentCycle.inhaleSeconds}s · Exhale ${currentCycle.exhaleSeconds}s · Hold ${currentCycle.holdSeconds}s",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            // Анимированный круг
            val circleColor = MaterialTheme.colorScheme.primary
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = circleColor,
                        radius = (size.minDimension / 2f) * scale.value,
                        alpha = 0.75f
                    )
                }
                Text(
                    text = phaseText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "${state.secondsRemaining}s",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(onClick = viewModel::onRestart) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Restart")
                }

                Spacer(Modifier.width(16.dp))

                Button(onClick = viewModel::onPause) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (state.isPaused) "Start" else "Pause")
                }
            }
        }
    }
}