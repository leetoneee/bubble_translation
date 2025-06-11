package com.bteamcoding.bubbletranslation.feature_home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.Inter

@Composable
fun TransModeButton(
    onClick: () -> Unit,
    enabled: Boolean = false,
    @DrawableRes icon: Int,
    content: String,
    description: String,
    contentColor: Color = Color.Black
) {
    if (enabled) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(71.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.blue_lightest)),
            shape = RoundedCornerShape(30.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.white)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                ) {
                    Text(
                        text = content,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = contentColor
                    )
                }
            }
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .height(71.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.white_light)),
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
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
}

@Preview
@Composable
fun TransModeButtonPreview() {
    TransModeButton(
        onClick = {},
        icon = R.drawable.baseline_android_24,
        content = "Dịch toàn\nmàn hình",
        description = "Dịch toàn bộ nội dung trên màn hình",
        enabled = true
    )
}


