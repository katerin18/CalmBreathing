package com.example.calmingbreath.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calmingbreath.data.ExerciseSessionDao
import com.example.calmingbreath.data.ExerciseSessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HeartRateInputScreenState(
    val insertionId: Long = 0L,
    val isAfterExercise: Boolean = false,
    val heartRateBefore: Int = 0,
    val heartRateAfter: Int = 0,
    val startTimestamp: Long = 0L,
    val endTimestamp: Long = 0L,
    val exerciseDurationSec: Long = 0L,
    val shouldNavigateToExercise: Boolean = false,
    val shouldNavigateToResults: Boolean = false
)

class HeartRateInputViewModel(val dao: ExerciseSessionDao) : ViewModel() {

    private val _state = MutableStateFlow(HeartRateInputScreenState())
    val state: StateFlow<HeartRateInputScreenState> = _state.asStateFlow()

    fun onBeginExercises() {
        _state.update { it.copy(
            shouldNavigateToExercise = true,
            startTimestamp = System.currentTimeMillis()
        ) }
    }

    fun onExerciseDone() {
        val endTime = System.currentTimeMillis()
        _state.update { it.copy(
            isAfterExercise = true,
            endTimestamp = endTime,
            exerciseDurationSec = (endTime - it.startTimestamp) / 1000
        ) }
    }

    fun addBpm(bpm: Int, isBefore: Boolean) {
        if (isBefore) {
            _state.update { it.copy(heartRateBefore = bpm) }
            viewModelScope.launch {
                val insertionId = dao.insertStartData(
                    ExerciseSessionEntity(bpmBefore = bpm)
                )
                _state.update { it.copy(insertionId = insertionId) }
            }
        } else {
            _state.update { it.copy(heartRateAfter = bpm) }
        }

    }

    fun updateExerciseNote() {
        viewModelScope.launch {
            dao.addDataAfterExercises(
                id = state.value.insertionId,
                bpmAfter = state.value.heartRateAfter,
                exerciseDuration = state.value.exerciseDurationSec,
                startExerciseTime = state.value.startTimestamp
            )
        }
    }


    fun onSeeResults() {
        if (state.value.heartRateAfter != 0) {
            _state.update { it.copy(shouldNavigateToResults = true) }
        }
    }

    fun onNavigationHandled() {
        _state.update { it.copy(shouldNavigateToExercise = false, shouldNavigateToResults = false) }
    }
}

class HeartRateInputViewModelFactory(
    private val dao: ExerciseSessionDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HeartRateInputViewModel(dao) as T
    }
}