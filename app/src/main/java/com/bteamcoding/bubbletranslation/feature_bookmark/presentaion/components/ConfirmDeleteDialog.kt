package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme

@Composable
fun ConfirmDeleteDialog(
    folder: Folder,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Xoá bộ thẻ?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Bạn có chắc chắn muốn xoá bộ thẻ \"${folder.name}\"? Hành động này không thể hoàn tác.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Nút hủy
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(
                            1.dp, color = colorResource(R.color.b_blue)
                        )
                    ) {
                        Text("Huỷ", color = colorResource(R.color.b_blue))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút xác nhận
                    Button(
                        onClick = onConfirm,
                        colors = ButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text("Xoá")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DeleteDialogPreview() {
    BubbleTranslationTheme {
        ConfirmDeleteDialog(
            folder = Folder("1", "avc", 1, false),
            onDismiss = {},
            onConfirm = {}
        )
    }
}
