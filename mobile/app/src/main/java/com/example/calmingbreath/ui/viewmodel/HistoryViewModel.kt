package com.example.calmingbreath.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calmingbreath.data.ExerciseSessionDao
import com.example.calmingbreath.data.ExerciseSessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(dao: ExerciseSessionDao) : ViewModel() {

    val history: StateFlow<List<ExerciseSessionEntity>> = dao.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

class HistoryViewModelFactory(
    private val dao: ExerciseSessionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(dao) as T
    }
}