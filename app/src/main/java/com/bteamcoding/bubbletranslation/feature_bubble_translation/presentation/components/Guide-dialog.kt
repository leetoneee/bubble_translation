package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.bteamcoding.bubbletranslation.R


@Composable
fun GuideDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: Int,
    contentColor: Color = Color.Black
) {
    val background = colorResource(R.color.blue_lightest)
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Help Icon",
                tint = contentColor
            )
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        containerColor = background,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGuideDialog() {
    GuideDialog(
        onDismissRequest = {},
        onConfirmation = {},
        dialogTitle = "Help Dialog",
        dialogText = "This is a help dialog with an icon.",
        icon = android.R.drawable.ic_dialog_info // Sử dụng một icon có sẵn từ Android SDK (ví dụ: ic_dialog_info)
    )
}