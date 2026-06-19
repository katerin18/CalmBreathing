package com.example.calmingbreath.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calmingbreath.MeasurementRequest
import com.example.calmingbreath.MeasurementsApi
import com.example.calmingbreath.RetrofitLogic
import com.example.calmingbreath.TokenManager
import com.example.calmingbreath.UserStore
import com.example.calmingbreath.data.ExerciseSessionDao
import com.example.calmingbreath.data.ExerciseSessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

class HeartRateInputViewModel(
    val dao: ExerciseSessionDao,
    private val measurementsApi: MeasurementsApi,
    private val userStore: UserStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HeartRateInputScreenState())
    val state: StateFlow<HeartRateInputScreenState> = _state.asStateFlow()

    // Читаем динамически: один и тот же VM живёт между сменами аккаунта.
    private fun currentUserId(): String = userStore.getUser()?.id ?: ""

    init {
        // При старте досылаем замеры, которые не успели уйти на бэкенд в прошлый раз.
        syncPendingMeasurements()
    }

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
                    ExerciseSessionEntity(bpmBefore = bpm, userId = currentUserId())
                )
                _state.update { it.copy(insertionId = insertionId) }
            }
        } else {
            _state.update { it.copy(heartRateAfter = bpm) }
        }

    }

    fun updateExerciseNote() {
        viewModelScope.launch {
            val id = state.value.insertionId
            dao.addDataAfterExercises(
                id = id,
                bpmAfter = state.value.heartRateAfter,
                exerciseDuration = state.value.exerciseDurationSec,
                startExerciseTime = state.value.startTimestamp
            )
            val s = state.value
            sendAndMark(
                id = id,
                startPulse = s.heartRateBefore,
                durationSec = s.exerciseDurationSec,
                endPulse = s.heartRateAfter,
                startMillis = s.startTimestamp
            )
        }
    }

    private fun syncPendingMeasurements() {
        val userId = currentUserId()
        if (userId.isEmpty()) return
        viewModelScope.launch {
            dao.getUnsynced(userId).forEach { e ->
                sendAndMark(
                    id = e.id,
                    startPulse = e.bpmBefore,
                    durationSec = e.exercisesDurationSec,
                    endPulse = e.bpmAfter,
                    startMillis = e.startExerciseTime
                )
            }
        }
    }

    // отправляем замер и при успехе помечаем synced. Падение сети не ломает локальную запись —
    // строка останется synced = 0 и будет дослана при следующем запуске.
    private suspend fun sendAndMark(
        id: Long,
        startPulse: Int,
        durationSec: Long,
        endPulse: Int,
        startMillis: Long,
    ) {
        try {
            measurementsApi.create(
                MeasurementRequest(
                    startPulse = startPulse,
                    exerciseDurationSeconds = durationSec,
                    endPulse = endPulse,
                    measuredAt = formatMeasuredAt(startMillis)
                )
            )
            dao.markSynced(id)
        } catch (e: Exception) {
            Log.e("Measurement", "send error = $e")
        }
    }

    private fun formatMeasuredAt(epochMillis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        return formatter.format(Date(epochMillis))
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
    private val dao: ExerciseSessionDao,
    private val context: Context,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val measurementsApi = RetrofitLogic.createMeasurementsApi(tokenManager)
        val userStore = UserStore(context)
        return HeartRateInputViewModel(dao, measurementsApi, userStore) as T
    }
}