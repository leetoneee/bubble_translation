package com.bteamcoding.bubbletranslation.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_home.component.HexagonButton
import com.bteamcoding.bubbletranslation.ui.theme.Inter

@Composable
fun SelectLang(
    modifier: Modifier = Modifier,
    shapeSize: Dp = 42.dp,
    textSize: TextUnit = 14.sp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        FilledIconButton(
            onClick = {},
            modifier = Modifier.size(shapeSize),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.blue_dark),
            )
        ) {
            Text("EN", fontSize = textSize, fontFamily = Inter, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        HexagonButton(
            width = shapeSize*4/5,
            height = shapeSize*4/5,
            icon = Icons.Filled.DoubleArrow,
            backgroundColor = colorResource(R.color.blue_medium),
            onClick = {}
        )
        FilledIconButton(
            onClick = {},
            modifier = Modifier.size(shapeSize),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.blue_dark),
            )
        ) {
            Text("VI", fontSize = textSize, fontFamily = Inter, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

