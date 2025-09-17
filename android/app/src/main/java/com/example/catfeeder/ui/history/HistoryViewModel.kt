package com.example.catfeeder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.FeedingRepository
import com.example.catfeeder.data.model.Feeding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val feedings: List<Feeding> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadHistory(TimeRange.LAST_24_HOURS)
    }

    fun loadHistory(timeRange: TimeRange) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val endTime = System.currentTimeMillis()
                val startTime = when (timeRange) {
                    TimeRange.LAST_24_HOURS -> endTime - 24 * 60 * 60 * 1000
                    TimeRange.LAST_7_DAYS -> endTime - 7 * 24 * 60 * 60 * 1000
                    TimeRange.LAST_30_DAYS -> endTime - 30 * 24 * 60 * 60 * 1000
                }
                val feedings = repository.getFeedings(startTime, endTime)
                _uiState.value = _uiState.value.copy(isLoading = false, feedings = feedings)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

enum class TimeRange {
    LAST_24_HOURS,
    LAST_7_DAYS,
    LAST_30_DAYS
}
