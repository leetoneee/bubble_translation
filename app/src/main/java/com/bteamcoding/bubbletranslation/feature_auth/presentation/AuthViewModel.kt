package com.bteamcoding.bubbletranslation.feature_auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.feature_auth.domain.use_case.SignInUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.use_case.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
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

            is AuthAction.OnUsernameChanged -> {
                _state.update { it.copy(username = action.value) }
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
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                signInUseCase(_state.value.email, _state.value.password)
            }.onSuccess { response ->
                Log.i("success", response.toString())
                if (response.code == 200) {
                    _state.update { it.copy(isLoading = false, user = response.result?.user, isLoginSuccessful = true) }
                } else {
                    _state.update { it.copy(isLoading = false, user = null, isLoginSuccessful = false, errorMessage = response.message) }
                }
            }.onFailure { t ->
                t.message?.let { Log.d("success", it) }
                _state.update { it.copy(isLoading = false, errorMessage = t.message) }
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                signUpUseCase(_state.value.username, _state.value.email, _state.value.password)
            }.onSuccess { response ->
                if (response.code == 200) {
                    _state.update { it.copy(isLoading = false, isSignUpSuccessful = true) }
                } else {
                    _state.update { it.copy(isLoading = false, isSignUpSuccessful = false, errorMessage = response.message) }
                }
            }.onFailure { t ->
                _state.update { it.copy(isLoading = false, errorMessage = t.message) }
            }
        }
    }
}