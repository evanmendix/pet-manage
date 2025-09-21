package com.supercatdev.catfeeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var isSigningIn = false

    fun signInAnonymously() {
        // Only sign in if there is no current user and a sign-in is not already in progress.
        if (authRepository.getCurrentUser() == null && !isSigningIn) {
            isSigningIn = true
            viewModelScope.launch {
                try {
                    authRepository.signInAnonymously()
                } finally {
                    isSigningIn = false
                }
            }
        }
    }
}
