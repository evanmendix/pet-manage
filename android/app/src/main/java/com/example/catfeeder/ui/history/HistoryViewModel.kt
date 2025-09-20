package com.example.catfeeder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.FeedingRepository
import com.example.catfeeder.data.PetRepository
import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val feedings: List<Feeding> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val managedPets: List<Pet> = emptyList(),
    val selectedPetId: String? = null,
    val timeRange: TimeRange = TimeRange.LAST_24_HOURS,
    // Placeholder
    val currentUserId: String = "user1"
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadPetsAndHistory()
    }

    fun loadPetsAndHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch pets and filter for managed ones
                val allPets = petRepository.getPets()
                val managedPets = allPets.filter { it.managingUserIds.contains(_uiState.value.currentUserId) }
                val selectedPetId = managedPets.firstOrNull()?.id

                _uiState.update {
                    it.copy(
                        managedPets = managedPets,
                        selectedPetId = selectedPetId
                    )
                }

                if (selectedPetId != null) {
                    loadHistoryForPet(selectedPetId, _uiState.value.timeRange)
                } else {
                    _uiState.update { it.copy(isLoading = false, feedings = emptyList()) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadHistoryForPet(petId: String, timeRange: TimeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val endTime = System.currentTimeMillis()
                val startTime = when (timeRange) {
                    TimeRange.LAST_24_HOURS -> endTime - 24 * 60 * 60 * 1000
                    TimeRange.LAST_7_DAYS -> endTime - 7 * 24 * 60 * 60 * 1000
                    TimeRange.LAST_30_DAYS -> endTime - 30 * 24 * 60 * 60 * 1000
                }
                val feedings = feedingRepository.getFeedings(petId, startTime, endTime)
                _uiState.update { it.copy(isLoading = false, feedings = feedings) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onPetSelected(petId: String) {
        _uiState.update { it.copy(selectedPetId = petId) }
        loadHistoryForPet(petId, _uiState.value.timeRange)
    }

    fun onTimeRangeSelected(timeRange: TimeRange) {
        _uiState.update { it.copy(timeRange = timeRange) }
        val petId = _uiState.value.selectedPetId
        if (petId != null) {
            loadHistoryForPet(petId, timeRange)
        }
    }
}

enum class TimeRange {
    LAST_24_HOURS,
    LAST_7_DAYS,
    LAST_30_DAYS
}
