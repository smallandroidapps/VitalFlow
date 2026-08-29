package com.location.vitalflow.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.location.vitalflow.data.repository.AuthManager
import com.location.vitalflow.data.repository.AuthResult
import com.location.vitalflow.data.repository.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val userEmail = preferenceManager.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _signInState = MutableStateFlow<AuthResult?>(null)
    val signInState: StateFlow<AuthResult?> = _signInState.asStateFlow()

    fun signIn(context: Context) {
        viewModelScope.launch {
            val result = authManager.signIn(context)
            _signInState.value = result
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            _signInState.value = null
        }
    }

    fun clearSignInState() {
        _signInState.value = null
    }
}
