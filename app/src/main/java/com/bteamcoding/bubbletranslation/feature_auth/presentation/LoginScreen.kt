package com.bteamcoding.bubbletranslation.feature_auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme

@Composable
fun LoginScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavToRegisterScreen: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Nếu đăng nhập thành công → gọi onBack
    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            viewModel.onAction(AuthAction.OnLoginSuccess)
            onBack()
        }
    }

    LoginScreen(
        state = state,
        onBack = onBack,
        onNavToRegisterScreen = onNavToRegisterScreen,
        onLoginClicked = {
            viewModel.onAction(AuthAction.OnLoginClicked)
        },
        onEmailChanged = {
            viewModel.onAction(AuthAction.OnEmailChanged(it))
        },
        onPasswordChanged = {
            viewModel.onAction(AuthAction.OnPasswordChanged(it))
        },
        onDismissError = {
            viewModel.onAction(AuthAction.ClearError)
            viewModel.onAction(AuthAction.OnPasswordChanged(""))
        }
    )
}

@Composable
fun LoginScreen(
    state: AuthState,
    onBack: () -> Unit,
    onNavToRegisterScreen: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onDismissError: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = Color(0xFFF2F1F7))
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        val (topBar, tfEmail, tfPassword, signInBtn, signUpText) = createRefs()
        val interactionSource = remember { MutableInteractionSource() }

        ConstraintLayout(
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                }
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            val (backBtn, title) = createRefs()

            FilledIconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back icon",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Black
                )
            }

            Text(
                text = "Sign In",
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(backBtn.end, margin = 8.dp)
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = { Text(text = "Email") },
            modifier = Modifier
                .constrainAs(tfEmail) {
                    top.linkTo(topBar.bottom, margin = 24.dp)
                }
                .fillMaxWidth()
        )

        PasswordTextField(
            password = state.password,
            onPasswordChange = onPasswordChanged,
            modifier = Modifier.constrainAs(tfPassword) {
                top.linkTo(tfEmail.bottom, margin = 16.dp)
            }
        )

        Box(
            modifier = Modifier
                .constrainAs(signInBtn) {
                    top.linkTo(tfPassword.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                    interactionSource = interactionSource
                ) {
                    onLoginClicked()
                }
                .background(
                    color = colorResource(R.color.b_blue),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(0.7f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sign in", fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Row(
            modifier = Modifier
                .constrainAs(signUpText) {
                    top.linkTo(signInBtn.bottom, margin = 16.dp)
                }
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                color = Color.DarkGray
            )
            Text(
                text = "Create now!",
                color = colorResource(R.color.b_blue), // hoặc Color(0xFF1565C0)
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavToRegisterScreen() }
            )
        }
    }

    if (!state.errorMessage.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = {
                // Đóng dialog và clear lỗi
                onDismissError()
            },
            title = {
                Text(text = "Đăng nhập thất bại", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = state.errorMessage ?: "")
            },
            confirmButton = {
                TextButton(onClick = {
                    // Clear lỗi khi đóng dialog
                    onDismissError()
                }) {
                    Text("OK")
                }
            },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray
        )
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0D12).copy(alpha = 0.4f))
                .zIndex(30f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Default.Visibility
            else
                Icons.Default.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    BubbleTranslationTheme {
        LoginScreen(
            state = AuthState(),
            onLoginClicked = {},
            onNavToRegisterScreen = {},
            onBack = {},
            onPasswordChanged = {},
            onEmailChanged = {},
            onDismissError = {}
        )
    }
}