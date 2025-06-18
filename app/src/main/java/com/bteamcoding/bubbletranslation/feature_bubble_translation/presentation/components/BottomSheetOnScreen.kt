package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetOnScreen(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sourceText: String,
    onTap: () -> Unit,
    textStyle: TextStyle = TextStyle(),
    itemPadding: Int = 10,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {

    val sheetState = rememberModalBottomSheetState()
    var isDragging by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
    ) {
        Surface(
            color = backgroundColor, modifier = modifier
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(itemPadding.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.sourceLang.value.countryName + " -> " + LanguageManager.targetLang.value.countryName,
                        style = textStyle,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* handle settings click */ }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { onDismissRequest() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(itemPadding.dp))

                // Từ gốc với icon copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = itemPadding.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = sourceText, style = textStyle, modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* handle copy action */ }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(itemPadding.dp))

                // Từ đã được dịch với icon copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = itemPadding.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Từ đã dịch", style = textStyle, modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* handle copy action */ }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(itemPadding.dp))

                // Divider
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            }

        }

    }

}
//
//@Preview(showBackground = true)
//@Composable
//fun BottomSheetOnScreenPreview() {
//    BottomSheetOnScreen(
//        onDismissRequest = { /* handle dismiss */ },
//        textStyle = MaterialTheme.typography.titleMedium,
//        itemPadding = 10,
//        backgroundColor = MaterialTheme.colorScheme.surface,
//        onTap = {}
//
//    )
//}
