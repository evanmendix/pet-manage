package com.example.catfeeder.ui.feeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.FeedingRepository
import com.example.catfeeder.data.model.Feeding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedingUiState(
    val currentStatus: Feeding? = null,
    val recentFeedings: List<Feeding> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDuplicateFeedingDialog: Boolean = false
)

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedingUiState(isLoading = true))
    val uiState: StateFlow<FeedingUiState> = _uiState

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getCurrentStatus().onSuccess { status ->
                _uiState.value = _uiState.value.copy(isLoading = false, currentStatus = status)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun addFeeding(type: String, force: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, showDuplicateFeedingDialog = false)
            val newFeeding = Feeding(userId = "testUser", timestamp = System.currentTimeMillis(), type = type)
            repository.addFeeding(newFeeding, force).onSuccess {
                // After adding, refresh the status to show the new feeding
                refreshStatus()
            }.onFailure {
                if (it.message?.contains("Duplicate feeding detected") == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, showDuplicateFeedingDialog = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissDuplicateFeedingDialog() {
        _uiState.value = _uiState.value.copy(showDuplicateFeedingDialog = false)
    }

    fun overwriteLastMeal(type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val newFeeding = Feeding(userId = "testUser", timestamp = System.currentTimeMillis(), type = type)
            repository.overwriteLastMeal(newFeeding).onSuccess {
                // After overwriting, refresh the status to show the new feeding
                refreshStatus()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }
}
