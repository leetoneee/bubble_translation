package com.bteamcoding.bubbletranslation.feature_auth.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
fun AccountScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavToLoginScreen: () -> Unit,
    onNavToProfileScreen: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user by viewModel.userInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(AuthAction.OnLoadCurrentUser)
    }

    AccountScreen(
        state = state,
        user = user,
        onBack = onBack,
        onNavToProfile = {
            if (user != null && user!!.id.toInt() != 0) {
                onNavToProfileScreen()
            } else {
                onNavToLoginScreen()
            }
        }
    )
}

@Composable
fun AccountScreen(
    state: AuthState,
    user: User?,
    onNavToProfile: () -> Unit,
    onBack: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = Color(0xFFF2F1F7))
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        val (topBar, profileGrp, controller) = createRefs()
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
                text = "Account",
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
                .constrainAs(profileGrp) {
                    top.linkTo(topBar.bottom, margin = 32.dp)
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                    interactionSource = interactionSource
                ) {
                    onNavToProfile()
                }
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (userIcon, label, nextIcon) = createRefs()

                Box(
                    modifier = Modifier
                        .constrainAs(userIcon) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .background(color = colorResource(R.color.b_orange), shape = CircleShape)
                        .padding(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Back icon",
                        modifier = Modifier
                            .size(36.dp),
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.constrainAs(label) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(userIcon.end, margin = 16.dp)
                    },
                ) {
                    Text(
                        textAlign = TextAlign.Left,
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            ) {
                                if (user != null && user.id.toInt() != 0) {
                                    append(user.username)
                                } else {
                                    append("Sign in")
                                }
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        textAlign = TextAlign.Left,
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            ) {
                                if (user != null && user.id.toInt() != 0) {
                                    append(user.email)
                                } else {
                                    append("to Bee Translation")
                                }
                            }
                        },
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go to edit",
                    modifier = Modifier
                        .constrainAs(nextIcon) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(30.dp)
                )
            }
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
                icon = Icons.Outlined.StarOutline,
                text = "Rate",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            FeatureGroup(
                icon = Icons.Outlined.Share,
                text = "Share",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            FeatureGroup(
                icon = Icons.Outlined.HelpOutline,
                text = "Help",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            FeatureGroup(
                icon = Icons.Outlined.Info,
                text = "About",
                onClick = {}
            )
        }
    }
}

@Composable
fun FeatureGroup(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                interactionSource = interactionSource
            ) {
                onClick()
            }
            .padding(vertical = 16.dp, horizontal = 16.dp)

    ) {
        val (ico, label) = createRefs()

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(R.color.b_blue),
            modifier = Modifier
                .constrainAs(ico) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(30.dp)
        )

        Text(
            modifier = Modifier
                .constrainAs(label) {
                    start.linkTo(ico.end, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            text = text,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp
        )

    }
}

@Composable
@Preview
fun AccountScreenPreview() {
    BubbleTranslationTheme {
        AccountScreen(
            state = AuthState(),
            user = User(
                id = 1,
                username = "Toan",
                email = "email",
                password = "111"
            ),
            onBack = {},
            onNavToProfile = {}
        )
    }
}