package com.supercatdev.catfeeder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.*
import com.supercatdev.catfeeder.data.model.Feeding
import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val feedings: List<Feeding> = emptyList(),
    val users: Map<String, User> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val managedPets: List<Pet> = emptyList(),
    val selectedPetId: String? = null,
    val timeRange: TimeRange = TimeRange.LAST_24_HOURS,
    // Current user's ID, fetched from AuthRepository
    val currentUserId: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        val currentUserId = authRepository.getCurrentUser()?.uid
        _uiState.update { it.copy(currentUserId = currentUserId) }
        loadPetsAndHistory()
    }

    fun loadPetsAndHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch pets and filter for managed ones
                val allPets = petRepository.getPets()
                val userId = _uiState.value.currentUserId
                val managedPets = if (userId != null) {
                    allPets.filter { it.managingUserIds.contains(userId) }
                } else {
                    emptyList()
                }
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
                val userIds = feedings.map { it.userId }.distinct()
                val users = userRepository.getUsers(userIds)
                _uiState.update { it.copy(isLoading = false, feedings = feedings, users = users) }
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
