package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R

@Composable
fun SubtitleOverlay(
    topPosition: Int,
    isRecognizing: Boolean,
    isTranslateMode: Boolean,
    subtitleText: String,
    onToggleTranslateMode: () -> Unit,
    onStartRecognition: () -> Unit,
    onStopRecognition: () -> Unit,
    onClose: () -> Unit,
) {
    val optionBarHeight = 40
    val borderColor = colorResource(R.color.b_yellow).copy(alpha = 0.8f)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Transparent)
    ) {
        val (optionBar, box) = createRefs()

        ConstraintLayout(
            modifier = Modifier
                .wrapContentWidth()
                .constrainAs(optionBar) {
                    if ((topPosition - optionBarHeight) > 0) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    } else {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                },
        ) {
            val (modeBtn, controlBtn, closeBtn) = createRefs()

            Box(
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, color = borderColor),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .constrainAs(modeBtn) {
                        end.linkTo(controlBtn.start, margin = 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        onToggleTranslateMode()
                    }
            ) {
                if (isTranslateMode) {
                    Icon(
                        imageVector = Icons.Rounded.TextFields,
                        contentDescription = "text field",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Translate,
                        contentDescription = "Translate",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(controlBtn) {
                        end.linkTo(closeBtn.start, margin = 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .border(
                        border = BorderStroke(1.dp, color = borderColor),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .clickable {
                        if (isRecognizing) {
                            onStopRecognition()
                        } else {
                            onStartRecognition()
                        }
                    }
            ) {
                if (isRecognizing) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = "Pause",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, color = borderColor),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .constrainAs(closeBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        onClose()
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close, // icon dấu X
                    contentDescription = "Close",
                    tint = borderColor,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(box) {
                    if ((topPosition.dp - optionBarHeight.dp) > 0.dp) {
                        top.linkTo(optionBar.bottom, margin = 8.dp)
                    } else {
                        bottom.linkTo(optionBar.top, margin = 8.dp)
                    }
                }
                .fillMaxWidth()
                .background(Color.Transparent, shape = RoundedCornerShape(6.dp))
                .border(
                    border = BorderStroke(1.dp, color = borderColor),
                    shape = RoundedCornerShape(6.dp)
                )
                .clip(RoundedCornerShape(6.dp))
                .wrapContentHeight()
                .heightIn(min = 50.dp, max = 110.dp),
            contentAlignment = Alignment.Center
        ) {
            if (subtitleText.isNotBlank()) {
                Text(
                    text = subtitleText,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(6.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SubtitleAreaPreview() {
    SubtitleOverlay(
        topPosition = 50,
        isRecognizing = true,
        subtitleText = "Hello!",
        onStopRecognition = {},
        onStartRecognition = {},
        onClose = {},
        onToggleTranslateMode = {},
        isTranslateMode = true
    )
}