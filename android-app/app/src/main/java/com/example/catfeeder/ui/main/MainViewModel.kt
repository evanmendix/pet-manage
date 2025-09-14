package com.example.catfeeder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        checkInitialStatus()
    }

    private fun checkInitialStatus() {
        viewModelScope.launch {
            try {
                val status = repository.getFeedingStatus()
                _uiState.value = MainUiState.Success(status.message)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun onFeedMealClicked() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                // First, check the status again to prevent race conditions
                val statusResponse = repository.getFeedingStatus()
                if (statusResponse.isFed) {
                    _uiState.value = MainUiState.Success(statusResponse.message)
                    return@launch
                }

                // If not fed, proceed to add a new feeding record
                val newFeeding = Feeding(
                    userId = "user-placeholder", // This should be replaced with the actual user ID
                    timestamp = System.currentTimeMillis(),
                    type = "meal"
                )
                repository.addFeeding(newFeeding)
                _uiState.value = MainUiState.Success("Successfully fed the cat!")

            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

// Represents the different states for the UI
sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(val message: String) : MainUiState
    data class Error(val errorMessage: String) : MainUiState
}
