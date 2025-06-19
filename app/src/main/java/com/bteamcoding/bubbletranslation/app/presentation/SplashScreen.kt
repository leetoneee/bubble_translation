package com.bteamcoding.bubbletranslation.app.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.Inter
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1600) // total duration of splash screen
        onAnimationEnd()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "fade-in-out"
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white_light))
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp)
            .graphicsLayer { this.alpha = alpha },
        verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(R.drawable.bee_group),
            contentDescription = null,
        )
        Column (
            modifier = Modifier
                .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Bee Translate",
                fontFamily = Inter,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp, color = colorResource(R.color.grey_light),)
            Text(
                text = "Ong Dịch Thuật",
                fontFamily = Inter,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.blue_medium))
            Text(text = "Những chú ong đồng hành cùng bạn\ntrên hành trình học ngoại ngữ",
                fontFamily = Inter, fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                color = colorResource(R.color.grey_light))
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(onAnimationEnd = {})
}