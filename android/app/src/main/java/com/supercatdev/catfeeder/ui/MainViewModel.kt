package com.supercatdev.catfeeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    init {
        // Automatically sign in if not already authenticated.
        if (authRepository.getCurrentUser() == null) {
            signInAnonymously()
        } else {
            _authState.value = AuthState.Success
        }
    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signInAnonymously()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                val errorMessage = "Firebase Authentication failed. \n\nPlease ensure your app is correctly configured with a `google-services.json` file and that Anonymous Authentication is enabled in the Firebase console."
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
}
