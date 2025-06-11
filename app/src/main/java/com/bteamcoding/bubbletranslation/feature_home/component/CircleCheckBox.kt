package com.bteamcoding.bubbletranslation.feature_home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.bteamcoding.bubbletranslation.R

@Composable
fun CircleCheckbox(selected: Boolean, enabled: Boolean = true, onChecked: () -> Unit) {

    val imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Circle
    val tint = if (selected) colorResource(R.color.blue_dark).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)
    val background = if (selected) Color.White else Color.Transparent

    IconButton(onClick = { onChecked() },
        modifier = Modifier
            .padding(0.dp),
        enabled = enabled) {

        Icon(imageVector = imageVector, tint = tint,
            modifier = Modifier.background(background, shape = CircleShape),
            contentDescription = "checkbox")
    }
}


//@Composable
//fun CircleCheckbox(
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    size: Dp = 24.dp,
//    borderColor: Color = Color.Gray,
//    checkedColor: Color = Color.Blue,
//    checkmarkColor: Color = Color.White
//) {
//    Box(
//        modifier = Modifier
//            .size(size)
//            .clip(CircleShape)
//            .background(if (checked) checkedColor else Color.Transparent)
//            .border(
//                width = 2.dp,
//                color = borderColor,
//                shape = CircleShape
//            )
//            .clickable { onCheckedChange(!checked) },
//        contentAlignment = Alignment.Center
//    ) {
//        if (checked) {
//            Icon(
//                imageVector = Icons.Default.Check,
//                contentDescription = null,
//                tint = checkmarkColor,
//                modifier = Modifier.size(size * 0.6f)
//            )
//        }
//    }
//}
