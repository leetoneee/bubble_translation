package com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordDialog(
    word: String,
    folders: List<Folder>,
    onConfirm: (Folder) -> Unit,
    onDismiss: () -> Unit
) {
    Log.i("folders", folders.toString())
    var expanded by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf<Folder?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Thêm từ vào bộ thẻ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                // Word (readonly)
                OutlinedTextField(
                    value = word,
                    onValueChange = {},
                    label = { Text("Từ vựng") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown to select folder
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedFolder?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chọn bộ thẻ") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .heightIn(max = 300.dp) // hạn chế chiều cao nếu danh sách dài
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        folders.forEachIndexed { index, folder ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = if (folder == selectedFolder)
                                                colorResource(R.color.blue_dark)
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = folder.name,
                                            fontWeight = if (folder == selectedFolder)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Normal,
                                            color = if (folder == selectedFolder)
                                                colorResource(R.color.blue_dark)
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedFolder = folder
                                    expanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            )

                            // Optional: divider giữa các folder
                            if (index < folders.lastIndex) {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(
                            1.dp, color = colorResource(
                                R.color.blue_dark
                            )
                        )
                    ) {
                        Text(
                            "Huỷ", color = colorResource(
                                R.color.blue_dark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            selectedFolder?.let { onConfirm(it) }
                        },
                        colors = ButtonColors(
                            containerColor = colorResource(
                                R.color.blue_dark
                            ),
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray
                        ),
                        enabled = selectedFolder != null && word != ""
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun AddWorđialogPreview() {
    BubbleTranslationTheme {
        AddWordDialog(
            onDismiss = {},
            onConfirm = {},
            word = "ABC",
            folders = listOf()
        )
    }
}