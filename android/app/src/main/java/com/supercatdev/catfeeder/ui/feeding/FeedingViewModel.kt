package com.supercatdev.catfeeder.ui.feeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.AuthRepository
import com.supercatdev.catfeeder.data.FeedingRepository
import com.supercatdev.catfeeder.data.PetRepository
import com.supercatdev.catfeeder.data.UserRepository
import com.supercatdev.catfeeder.data.model.CreateFeedingRequest
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

data class FeedingUiState(
    val currentStatus: Feeding? = null,
    val currentFeeder: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDuplicateFeedingDialog: Boolean = false,
    val pendingFeedingType: String? = null, // Store the type of feeding that triggered the dialog
    val managedPets: List<Pet> = emptyList(),
    val selectedPetId: String? = null,
    val currentUserId: String? = null
)

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedingUiState(isLoading = true))
    val uiState: StateFlow<FeedingUiState> = _uiState.asStateFlow()

    init {
        val currentUserId = authRepository.getCurrentUser()?.uid
        _uiState.update { it.copy(currentUserId = currentUserId) }
        refreshAllData()
    }

    private fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentFeeder = null) }
            try {
                val allPets = petRepository.getPets()
                val userId = _uiState.value.currentUserId
                val managedPets = if (userId != null) {
                    allPets.filter { it.managingUserIds.contains(userId) }
                } else {
                    emptyList()
                }

                val currentSelectedId = _uiState.value.selectedPetId
                val newSelectedPetId = when {
                    managedPets.size == 1 -> managedPets.first().id
                    managedPets.any { it.id == currentSelectedId } -> currentSelectedId
                    else -> null
                }

                _uiState.update { it.copy(managedPets = managedPets, selectedPetId = newSelectedPetId) }

                if (newSelectedPetId != null) {
                    feedingRepository.getCurrentStatus(newSelectedPetId).onSuccess { status ->
                        if (status != null) {
                            val users = userRepository.getUsers(listOf(status.userId))
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    currentStatus = status,
                                    currentFeeder = users[status.userId]
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, currentStatus = null, currentFeeder = null) }
                        }
                    }.onFailure { throwable ->
                        _uiState.update { it.copy(isLoading = false, error = throwable.message, currentStatus = null, currentFeeder = null) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, currentStatus = null, currentFeeder = null) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectPet(petId: String) {
        _uiState.update { it.copy(selectedPetId = petId) }
        refreshAllData() // Refresh status when pet is selected
    }

    fun addFeeding(type: String, force: Boolean = false) {
        val petId = uiState.value.selectedPetId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showDuplicateFeedingDialog = false) }
            val request = CreateFeedingRequest(
                petId = petId,
                timestamp = System.currentTimeMillis(),
                type = type
            )
            feedingRepository.addFeeding(request, force).onSuccess {
                refreshAllData()
            }.onFailure { throwable ->
                if (throwable.message?.contains("Duplicate feeding detected") == true) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showDuplicateFeedingDialog = true,
                            pendingFeedingType = type
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
            }
        }
    }

    fun dismissDuplicateFeedingDialog() {
        _uiState.update { it.copy(showDuplicateFeedingDialog = false, pendingFeedingType = null) }
    }

    fun overwriteLastMeal(type: String) {
        val petId = uiState.value.selectedPetId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = CreateFeedingRequest(
                petId = petId,
                timestamp = System.currentTimeMillis(),
                type = type
            )
            feedingRepository.overwriteLastMeal(request).onSuccess {
                refreshAllData()
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }
}