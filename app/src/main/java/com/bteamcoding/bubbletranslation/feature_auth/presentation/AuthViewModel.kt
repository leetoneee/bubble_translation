package com.bteamcoding.bubbletranslation.feature_auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetUserInfoUseCase
import com.bteamcoding.bubbletranslation.app.domain.use_case.LogoutUseCase
import com.bteamcoding.bubbletranslation.app.domain.use_case.SaveUserInfoUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import com.bteamcoding.bubbletranslation.feature_auth.domain.use_case.DeleteUserUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.use_case.SignInUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.use_case.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = action.value) }
            }

            is AuthAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.value, errorMessage = null) }
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

            AuthAction.OnLoadCurrentUser -> getUserInfo()

            AuthAction.OnLogOut -> {
                _state.update {
                    it.copy(
                        user = null,
                        isLoginSuccessful = false,
                        errorMessage = null
                    )
                }
                logout()
            }

            AuthAction.OnLoginSuccess -> {
                _state.update {
                    it.copy(
                        isLoginSuccessful = false,
                        errorMessage = null,
                        email = "",
                        password = ""
                    )
                }
            }

            AuthAction.OnRegisterSuccess -> {
                _state.update {
                    it.copy(
                        isSignUpSuccessful = false,
                        errorMessage = null,
                        username = "",
                        confirmPassword = "",
                        successMessage = null
                    )
                }
            }

            is AuthAction.OnShowConfirmDialog -> {
                _state.update { it.copy(showConfirmDialog = action.value) }
            }

            AuthAction.OnDeleteAccount -> deleteAccount()

            AuthAction.OnDeleteSuccess -> {
                _state.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = null,
                        showConfirmDialog = false
                    )
                }
            }
        }
    }

    private fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.update {
                it.copy(errorMessage = "Email và mật khẩu không được để trống")
            }
            return
        }

        if (password.length < 6) {
            _state.update {
                it.copy(errorMessage = "Mật khẩu phải có ít nhất 6 ký tự")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                signInUseCase(email, password)
            }.onSuccess { response ->
                Log.i("success", response.toString())
                if (response.code == 200) {
                    val user = response.result?.user
                    if (user != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                user = user,
                                isLoginSuccessful = true,
                            )
                        }
                        saveUserInfo(user.id, user.username, user.email)
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = null,
                            isLoginSuccessful = false,
                            errorMessage = response.message
                        )
                    }
                }
            }.onFailure { t ->
                t.message?.let { Log.d("success", it) }
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Lỗi không xác định"
                    )
                }
            }
        }
    }

    private fun register() {
        val error = validateRegisterInput()
        if (error != null) {
            _state.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                signUpUseCase(_state.value.username, _state.value.email, _state.value.password)
            }.onSuccess { response ->
                if (response.code == 200) {
                    val user = response.result?.user
                    if (user != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSignUpSuccessful = true,
                                successMessage = "Đăng ký thành công"
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSignUpSuccessful = false,
                            errorMessage = response.message
                        )
                    }
                }
            }.onFailure { t ->
                _state.update { it.copy(isLoading = false, errorMessage = t.message) }
            }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                _userInfo.value?.let { deleteUserUseCase(it.id) }
            }.onSuccess { response ->
                _state.update { it.copy(isLoading = false, successMessage = "Xoá tài khoản thành công") }
            }.onFailure { t ->
                _state.update { it.copy(isLoading = false, errorMessage = t.message) }
            }
        }
    }

    private fun validateRegisterInput(): String? {
        val username = _state.value.username.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword

        return when {
            username.isEmpty() -> "Vui lòng nhập tên người dùng"
            email.isEmpty() -> "Vui lòng nhập email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            password.isEmpty() -> "Vui lòng nhập mật khẩu"
            password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            confirmPassword.isEmpty() -> "Vui lòng xác nhận mật khẩu"
            confirmPassword != password -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
    }

    private fun saveUserInfo(id: Long, username: String, email: String) {
        viewModelScope.launch {
            saveUserInfoUseCase(id, username, email)
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { user ->
                _userInfo.value = user
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}