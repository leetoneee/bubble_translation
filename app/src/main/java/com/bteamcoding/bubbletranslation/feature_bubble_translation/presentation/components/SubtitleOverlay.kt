package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubtitleOverlay(
    isRecognizing: Boolean,
    subtitleText: String,
    onStartRecognition: () -> Unit,
    onStopRecognition: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .background(Color.White, shape = RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {
                if (isRecognizing) {
                    onStopRecognition()
                } else {
                    onStartRecognition()
                }
            }) {
                Text(if (isRecognizing) "🛑 Dừng" else "🎤 Bắt đầu")
            }

            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(100.dp)
                    .padding(bottom = 50.dp)
                    .background(Color.Red),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (subtitleText.isNotBlank()) {
                    Text(
                        text = subtitleText,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SubtitleAreaPreview() {
    SubtitleOverlay(
        isRecognizing = true,
        subtitleText = "Hello!",
        onStopRecognition = {},
        onStartRecognition = {}
    )
}