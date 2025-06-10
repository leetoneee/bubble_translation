package com.bteamcoding.bubbletranslation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R

@Composable
fun TopBar(
    title: String,
    onNavToAuthScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        val (name, infoBtn, settingBtn, userBtn) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        ) {
            Text(
                text = title,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }

        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.b_gray),
            ),
            modifier = Modifier.constrainAs(infoBtn) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(settingBtn.start, margin = 8.dp)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colorResource(R.color.b_blue)
            )
        }

        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.b_gray),
            ),
            modifier = Modifier.constrainAs(settingBtn) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(userBtn.start, margin = 8.dp)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colorResource(R.color.b_blue)
            )
        }

        FilledIconButton(
            onClick = onNavToAuthScreen,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.b_gray),
            ),
            modifier = Modifier.constrainAs(userBtn) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colorResource(R.color.b_blue)
            )
        }
    }
}


@Composable
@Preview
fun TopBarPreview() {
    TopBar("Image Recognition", onNavToAuthScreen = {})
}