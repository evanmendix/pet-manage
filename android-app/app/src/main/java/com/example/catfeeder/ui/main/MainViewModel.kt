package com.example.catfeeder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.catfeeder.data.UserManager
import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.repository.FeedingRepository
import com.example.catfeeder.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val storageRepository: StorageRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                val feedings = feedingRepository.getFeedings()
                val status = feedingRepository.getFeedingStatus()
                _uiState.value = MainUiState.Success(status.message, feedings)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun onFeedMealClicked(photoUri: Uri? = null) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                // First, check the status again to prevent race conditions
                val statusResponse = feedingRepository.getFeedingStatus()
                if (statusResponse.isFed) {
                    val feedings = feedingRepository.getFeedings()
                    _uiState.value = MainUiState.Success(statusResponse.message, feedings)
                    return@launch
                }

                val photoUrl = if (photoUri != null) {
                    storageRepository.uploadImage(photoUri)
                } else {
                    null
                }

                // If not fed, proceed to add a new feeding record
                val userId = userManager.currentUser?.id
                if (userId == null) {
                    _uiState.value = MainUiState.Error("User not logged in")
                    return@launch
                }
                val newFeeding = Feeding(
                    userId = userId,
                    timestamp = System.currentTimeMillis(),
                    type = "meal",
                    photoUrl = photoUrl
                )
                feedingRepository.addFeeding(newFeeding)

                // Refresh the list
                val feedings = feedingRepository.getFeedings()
                _uiState.value = MainUiState.Success("Successfully fed the cat!", feedings)

            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

// Represents the different states for the UI
sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(
        val message: String,
        val feedings: List<Feeding> = emptyList()
    ) : MainUiState
    data class Error(val errorMessage: String) : MainUiState
}
