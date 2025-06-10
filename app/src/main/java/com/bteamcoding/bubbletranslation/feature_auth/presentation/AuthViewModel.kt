package com.bteamcoding.bubbletranslation.feature_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = action.value) }
            }

            is AuthAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.value) }
            }

            is AuthAction.OnPasswordChanged -> {
                _state.update { it.copy(password = action.value) }
            }

            AuthAction.OnLoginClicked -> login()

            AuthAction.OnRegisterClicked -> register()

            AuthAction.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update {it.copy(isLoading = true, errorMessage = null)}

//            delay(1000) // giả lập API call

            if (_state.value.email == "test@example.com" && _state.value.password == "123456") {
                _state.update {it.copy(isLoading = false, isLoginSuccessful = true)}
            } else {
                _state.update {it.copy(isLoading = false, errorMessage = "Sai email hoặc mật khẩu")}
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update {it.copy(isLoading = true, errorMessage = null)}

//            delay(1000) // giả lập API call

            if (_state.value.password == _state.value.confirmPassword) {
                _state.update {it.copy(isLoading = false, isLoginSuccessful = true)}
            } else {
                _state.update {it.copy(isLoading = false, errorMessage = "Mật khẩu không trùng khớp")}
            }
        }
    }
}