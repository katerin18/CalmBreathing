package com.example.calmingbreath.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calmingbreath.MeasurementResponse
import com.example.calmingbreath.MeasurementsApi
import com.example.calmingbreath.RetrofitLogic
import com.example.calmingbreath.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val items: List<MeasurementResponse> = emptyList(),
    val isError: Boolean = false,
)

class HistoryViewModel(
    private val measurementsApi: MeasurementsApi,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                // measuredAt в ISO-формате сортируется лексикографически = хронологически.
                val items = measurementsApi.list().sortedByDescending { it.measuredAt }
                _state.update { it.copy(isLoading = false, items = items) }
            } catch (e: Exception) {
                Log.e("History", "load error = $e")
                _state.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }
}

class HistoryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val measurementsApi = RetrofitLogic.createMeasurementsApi(tokenManager)
        return HistoryViewModel(measurementsApi) as T
    }
}