package com.bteamcoding.bubbletranslation.feature_auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme

@Composable
fun RegisterScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterScreen(
        state = state,
        onBack = onBack,
        onRegisterClicked = {
            viewModel.onAction(AuthAction.OnRegisterClicked)
        },
        onUsernameChanged = {
            viewModel.onAction(AuthAction.OnUsernameChanged(it))
        },
        onEmailChanged = {
            viewModel.onAction(AuthAction.OnEmailChanged(it))
        },
        onPasswordChanged = {
            viewModel.onAction(AuthAction.OnPasswordChanged(it))
        },
        onConfirmPasswordChanged = {
            viewModel.onAction(AuthAction.OnConfirmPasswordChanged(it))
        },
        onDismissError = {
            viewModel.onAction(AuthAction.ClearError)
            viewModel.onAction(AuthAction.OnConfirmPasswordChanged(""))
        },
        onSuccess = {
            if (state.isSignUpSuccessful) {
                viewModel.onAction(AuthAction.OnRegisterSuccess)
                onBack()
            }
        }
    )
}

@Composable
fun RegisterScreen(
    state: AuthState,
    onBack: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    onDismissError: () -> Unit,
    onSuccess: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = Color(0xFFF2F1F7))
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        val (topBar, tfEmail, tfUsername, tfPassword, tfConfirm, signInBtn) = createRefs()
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
                text = "Sign Up",
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

        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChanged,
            label = { Text(text = "Username") },
            modifier = Modifier
                .constrainAs(tfUsername) {
                    top.linkTo(tfEmail.bottom, margin = 24.dp)
                }
                .fillMaxWidth()
        )

        PasswordTextField(
            password = state.password,
            onPasswordChange = onPasswordChanged,
            modifier = Modifier.constrainAs(tfPassword) {
                top.linkTo(tfUsername.bottom, margin = 16.dp)
            }
        )

        PasswordTextField(
            password = state.confirmPassword,
            onPasswordChange = onConfirmPasswordChanged,
            label = "Confirm Password",
            modifier = Modifier.constrainAs(tfConfirm) {
                top.linkTo(tfPassword.bottom, margin = 16.dp)
            }
        )

        Box(
            modifier = Modifier
                .constrainAs(signInBtn) {
                    top.linkTo(tfConfirm.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                    interactionSource = interactionSource
                ) {
                    onRegisterClicked()
                }
                .background(
                    color = colorResource(R.color.b_blue),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(0.7f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sign up", fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }

    if (!state.errorMessage.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = {
                // Đóng dialog và clear lỗi
                onDismissError()
            },
            title = {
                Text(text = "Đăng ký thất bại", fontWeight = FontWeight.Bold)
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

    if (!state.successMessage.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = onSuccess,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50), // màu xanh lá
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.successMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onSuccess) {
                    Text("OK", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
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

@Preview
@Composable
fun RegisterScreenPreview() {
    BubbleTranslationTheme {
        RegisterScreen(
            state = AuthState(),
            onRegisterClicked = {},
            onBack = {},
            onPasswordChanged = {},
            onEmailChanged = {},
            onUsernameChanged = {},
            onConfirmPasswordChanged = {},
            onDismissError = {},
            onSuccess = {}
        )
    }
}