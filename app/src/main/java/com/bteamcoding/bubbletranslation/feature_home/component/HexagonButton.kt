package com.bteamcoding.bubbletranslation.feature_home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.Inter
import androidx.compose.runtime.getValue

@Composable
fun HexagonButton(
    width: Dp,
    height: Dp,
    icon: ImageVector? = null,
    text: String? = null,
    backgroundColor: Color = Color.Blue,
    contentColor: Color = Color.White,
    textStyle: TextStyle = TextStyle(fontSize = 12.sp),
    onClick: () -> Unit
) {
    require(icon != null || text != null) { "Either icon or text must be provided" }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .drawWithCache {
                val polygon = RoundedPolygon(
                    numVertices = 6,
                    radius = minOf(size.width, size.height) / 2f,
                    centerX = size.width / 2f,
                    centerY = size.height / 2f,
                    rounding = CornerRounding(
                        size.minDimension / 10f,
                        smoothing = 0.1f
                    )
                )
                val path = polygon.toPath().asComposePath()
                onDrawBehind {
                    drawPath(path, backgroundColor)
                }
            }
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size((width*1/2)))
        } else if (text != null) {
            Text(text = text, color = contentColor, style = textStyle)
        }
    }
}

@Preview
@Composable
fun PreviewHexagonButton() {
    HexagonButton(
        width = 100.dp,
        height = 100.dp,
        icon = Icons.Filled.PowerSettingsNew,
        backgroundColor = colorResource(R.color.blue_dark),
        textStyle = TextStyle(fontSize = 12.sp, color = Color.White, fontFamily = Inter, fontWeight = FontWeight.ExtraBold),
        onClick = {}
    )
}




