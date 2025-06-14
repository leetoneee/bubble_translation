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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme

@Composable
fun ProfileScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user by viewModel.userInfo.collectAsStateWithLifecycle()

    user?.let {
        ProfileScreen(
            state = state,
            user = it,
            onBack = onBack,
            onSignOut = {
                viewModel.onAction(AuthAction.OnLogOut)
                onBack()
            },
            onDeleteClicked = {
                viewModel.onAction(AuthAction.OnShowConfirmDialog(true))
            },
            onConfirm = {
                viewModel.onAction(AuthAction.OnDeleteAccount)
            },
            onDismiss = {
                viewModel.onAction(AuthAction.OnShowConfirmDialog(false))
            },
            onSuccess = {
                if (state.successMessage != null) {
                    viewModel.onAction(AuthAction.OnDeleteSuccess)
                    viewModel.onAction(AuthAction.OnLogOut)
                    onBack()
                }
            }
        )
    }
}

@Composable
fun ProfileScreen(
    state: AuthState,
    user: User,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteClicked: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSuccess: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = Color(0xFFF2F1F7))
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        val (topBar, avtIcon, profileGrp, controller, signOutBtn) = createRefs()
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
                text = "My Account",
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(backBtn.end, margin = 8.dp)
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(avtIcon) {
                    top.linkTo(topBar.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(color = colorResource(R.color.b_orange), shape = CircleShape)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Back icon",
                modifier = Modifier
                    .size(64.dp),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(profileGrp) {
                    top.linkTo(avtIcon.bottom, margin = 24.dp)
                }
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileItem(
                label = "Username",
                value = user.username,
                icon = Icons.Default.Person
            )

            HorizontalDivider()

            ProfileItem(
                label = "Email",
                value = user.email,
                icon = Icons.Default.Email
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(controller) {
                    top.linkTo(profileGrp.bottom, margin = 16.dp)
                }
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth(),
        ) {
            FeatureGroup(
                icon = Icons.Outlined.Edit,
                text = "Manage account",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            FeatureGroup(
                icon = Icons.Outlined.DeleteOutline,
                text = "Delete account",
                onClick = onDeleteClicked
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(signOutBtn) {
                    top.linkTo(controller.bottom, margin = 16.dp)
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                    interactionSource = interactionSource
                ) {
                    onSignOut()
                }
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sign out", fontWeight = FontWeight.SemiBold, color = Color.Red)
        }
    }

    if (state.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Xác nhận xoá tài khoản",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xoá tài khoản? Hành động này không thể hoàn tác.",
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Xoá", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Huỷ", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
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

}

@Composable
fun ProfileItem(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = colorResource(R.color.b_blue))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    BubbleTranslationTheme {
        ProfileScreen(
            state = AuthState(),
            user = User(1, "letoan", "letoan7442", "0000"),
            onBack = {},
            onSignOut = {},
            onDeleteClicked = {},
            onConfirm = {},
            onDismiss = {},
            onSuccess = {}
        )
    }
}