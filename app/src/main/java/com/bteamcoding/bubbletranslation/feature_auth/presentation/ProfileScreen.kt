package com.bteamcoding.bubbletranslation.feature_auth.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user by viewModel.userInfo.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onSignOut = {
            viewModel.onAction(AuthAction.OnLogOut)
            onBack()
        }
    )
}

@Composable
fun ProfileScreen(
    state: AuthState,
    onSignOut: () -> Unit
) {
    FilledIconButton(
        onClick = onSignOut,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color(0xFFF3F2F7).copy(
                alpha = 0.7f
            )
        ),
        modifier = Modifier
            .size(50.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBackIosNew,
            contentDescription = "Back",
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
    }
}