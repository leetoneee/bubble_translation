package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R
import java.time.format.TextStyle

@Composable
fun FABItemUI(
    item: FABItem,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .border(2.dp, colorResource(R.color.b_blue), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.b_blue),
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier
            .width(10.dp)
            .background(Color.Transparent))

        FloatingActionButton(
            onClick = item.onClick,
            containerColor = colorResource(R.color.b_blue),
            modifier = Modifier.size(45.dp)
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = Color.White)
        }
    }
}