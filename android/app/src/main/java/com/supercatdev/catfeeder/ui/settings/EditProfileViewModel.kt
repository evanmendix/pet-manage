package com.supercatdev.catfeeder.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.AuthRepository
import com.supercatdev.catfeeder.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

data class EditProfileUiState(
    val userName: String = "",
    val profilePictureUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdateSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    @Named("BaseImageUrl") private val baseImageUrl: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser != null) {
                try {
                    val user = userRepository.getUsers(listOf(firebaseUser.uid))[firebaseUser.uid]
                    _uiState.update {
                        it.copy(
                            userName = user?.name ?: "",
                            profilePictureUrl = user?.profilePictureUrl?.let { path -> "$baseImageUrl$path" },
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(error = "User not logged in", isLoading = false) }
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isUpdateSuccess = false) }
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser == null) {
                _uiState.update { it.copy(error = "User not logged in", isLoading = false) }
                return@launch
            }

            try {
                userRepository.updateUser(firebaseUser.uid, name)
                _uiState.update {
                    it.copy(
                        userName = name,
                        isLoading = false,
                        isUpdateSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser == null) {
                _uiState.update { it.copy(error = "User not logged in", isLoading = false) }
                return@launch
            }

            try {
                val relativeUrl = userRepository.uploadProfilePicture(firebaseUser.uid, uri)
                _uiState.update {
                    it.copy(
                        profilePictureUrl = "$baseImageUrl$relativeUrl",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}