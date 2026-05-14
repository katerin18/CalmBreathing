package com.example.calmingbreath.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BreathingPhase { INHALE, EXHALE, HOLD }

data class BreathingCycle(
    val inhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdSeconds: Int,
    val stage: Int
)

data class ExerciseScreenState(
    val currentCycleIndex: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.INHALE,
    val secondsRemaining: Int = 0,
    val isPaused: Boolean = true,
    val isFinished: Boolean = false,
    val shouldNavigateNext: Boolean = false
)

class ExerciseViewModel : ViewModel() {

    val cycles: List<BreathingCycle> = listOf(
        // Stage I
        BreathingCycle(4, 4, 2, 1),
        BreathingCycle(4, 5, 2, 1),
        BreathingCycle(4, 6, 2, 1),
        BreathingCycle(4, 7, 2, 1),
        BreathingCycle(4, 8, 2, 1),
        // Stage II
        BreathingCycle(4, 9, 2, 2),
        BreathingCycle(5, 9, 2, 2),
        BreathingCycle(5, 10, 2, 2),
        // Stage III
        BreathingCycle(6, 10, 3, 3),
        BreathingCycle(7, 10, 3, 3),
        BreathingCycle(8, 10, 4, 3),
        BreathingCycle(9, 10, 4, 3),
        BreathingCycle(10, 10, 5, 3),
        // Stage IV
        BreathingCycle(9, 10, 4, 4),
        BreathingCycle(8, 9, 4, 4),
        BreathingCycle(7, 8, 3, 4),
        BreathingCycle(6, 7, 3, 4),
        BreathingCycle(5, 6, 2, 4),
        // Stage V
        BreathingCycle(4, 5, 2, 5),
        BreathingCycle(4, 4, 2, 5)
    )

    private val _state = MutableStateFlow(ExerciseScreenState())
    val state: StateFlow<ExerciseScreenState> = _state.asStateFlow()

    private var exerciseJob: Job? = null
    private var pausedAtCycleIndex = 0
    private var pausedAtPhase = BreathingPhase.INHALE

    private fun startExercise(
        fromCycleIndex: Int = 0,
        fromPhase: BreathingPhase = BreathingPhase.INHALE
    ) {
        exerciseJob = viewModelScope.launch {
            var cycleIdx = fromCycleIndex
            var phase = fromPhase

            while (cycleIdx < cycles.size) {
                val cycle = cycles[cycleIdx]
                val phaseDuration = when (phase) {
                    BreathingPhase.INHALE -> cycle.inhaleSeconds
                    BreathingPhase.EXHALE -> cycle.exhaleSeconds
                    BreathingPhase.HOLD -> cycle.holdSeconds
                }

                _state.update {
                    it.copy(
                        currentCycleIndex = cycleIdx,
                        currentPhase = phase,
                        secondsRemaining = phaseDuration
                    )
                }

                repeat(phaseDuration) {
                    delay(1000L)
                    _state.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
                }

                phase = when (phase) {
                    BreathingPhase.INHALE -> BreathingPhase.EXHALE
                    BreathingPhase.EXHALE -> BreathingPhase.HOLD
                    BreathingPhase.HOLD -> {
                        cycleIdx++
                        BreathingPhase.INHALE
                    }
                }
            }

            _state.update { it.copy(isFinished = true, shouldNavigateNext = true) }
        }
    }

    fun onPause() {
        if (_state.value.isPaused) {
            _state.update { it.copy(isPaused = false) }
            startExercise(pausedAtCycleIndex, pausedAtPhase)
        } else {
            pausedAtCycleIndex = _state.value.currentCycleIndex
            pausedAtPhase = _state.value.currentPhase
            exerciseJob?.cancel()
            _state.update { it.copy(isPaused = true) }
        }
    }

    fun onRestart() {
        exerciseJob?.cancel()
        pausedAtCycleIndex = 0
        pausedAtPhase = BreathingPhase.INHALE
        _state.update { it.copy(isPaused = false, isFinished = false, shouldNavigateNext = false) }
        startExercise()
    }

    fun onNavigationHandled() {
        _state.update { it.copy(shouldNavigateNext = false) }
    }

    override fun onCleared() {
        super.onCleared()
        exerciseJob?.cancel()
    }
}