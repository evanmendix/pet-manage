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

    fun signInAnonymously() {
        // Only sign in if there is no current user
        if (authRepository.getCurrentUser() == null) {
            viewModelScope.launch {
                authRepository.signInAnonymously()
            }
        }
    }
}
