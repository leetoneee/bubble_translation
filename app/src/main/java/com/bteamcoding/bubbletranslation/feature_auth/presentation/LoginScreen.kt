package com.bteamcoding.bubbletranslation.feature_auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onLoginClicked = {
            viewModel.onAction(AuthAction.OnLoginClicked)
        },
        onEmailChanged = {
            viewModel.onAction(AuthAction.OnEmailChanged(it))
        },
        onRegisterClicked = {
            viewModel.onAction(AuthAction.OnRegisterClicked)
        },
        onPasswordChanged = {
            viewModel.onAction(AuthAction.OnPasswordChanged(it))
        }
    )
}

@Composable
fun LoginScreen(
    state: AuthState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = { Text(text = "Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = { Text(text = "Password") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Row {
                Button(onClick = onLoginClicked) {
                    Text("Đăng nhập")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRegisterClicked) {
                    Text("Đăng ký")
                }
            }
        }

        state.errorMessage?.let { Text(text = it, color = Color.Red) }
        state.user?.let { user ->
            Text(text = user.username)
            Text(text = user.email)
        }
    }
}