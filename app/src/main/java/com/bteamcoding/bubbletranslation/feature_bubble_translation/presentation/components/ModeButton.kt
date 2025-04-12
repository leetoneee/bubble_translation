package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R

@Composable
fun ModeButton(
    onClick: () -> Unit,
    enabled: Boolean = false,
    buttonColor: Color = colorResource(R.color.b_gray),
    @DrawableRes icon: Int,
    content: String,
    contentColor: Color = Color.Black
) {
    if (enabled) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(buttonColor),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = content,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = contentColor
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.b_gray)),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = content,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun ModeButtonPreview() {
    ModeButton(
        onClick = {},
        icon = R.drawable.baseline_android_24,
        content = "Dịch toàn\nmàn hình",
        enabled = false
    )
}