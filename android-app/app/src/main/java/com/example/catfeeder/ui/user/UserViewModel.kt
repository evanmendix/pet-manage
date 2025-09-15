package com.example.catfeeder.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.model.User
import com.example.catfeeder.data.repository.AuthRepository
import com.example.catfeeder.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            var uid = authRepository.getCurrentUserUid()
            if (uid == null) {
                uid = authRepository.signInAnonymously()
            }

            val user = userRepository.getUser(uid)
            if (user == null) {
                _uiState.value = UserUiState.NeedsSetup(uid)
            } else {
                _uiState.value = UserUiState.UserExists(user)
            }
        }
    }

    fun createUser(uid: String, name: String) {
        viewModelScope.launch {
            try {
                val newUser = User(id = uid, name = name)
                val createdUser = userRepository.createUser(newUser)
                _uiState.value = UserUiState.UserExists(createdUser)
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error(e.message ?: "Failed to create user")
            }
        }
    }
}

sealed interface UserUiState {
    object Loading : UserUiState
    data class NeedsSetup(val uid: String) : UserUiState
    data class UserExists(val user: User) : UserUiState
    data class Error(val message: String) : UserUiState
}
