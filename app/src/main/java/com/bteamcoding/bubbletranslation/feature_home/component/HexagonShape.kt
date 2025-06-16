package com.bteamcoding.bubbletranslation.feature_home.component

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val radius = min(width, height) / 2f
        val centerX = width / 2f
        val centerY = height / 2f

        val path = Path().apply {
            for (i in 0..5) {
                val angle = Math.toRadians((60 * i + 60).toDouble()) // ⬅️ rotated 90°
                val x = (centerX + radius * cos(angle)).toFloat()
                val y = (centerY + radius * sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        return Outline.Generic(path)
    }
}
