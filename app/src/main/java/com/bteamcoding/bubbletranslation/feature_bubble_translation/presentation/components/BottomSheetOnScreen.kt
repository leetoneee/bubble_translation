package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components


import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.CreateFolderDialog
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryAction
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryScreen
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryViewModel
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryViewModel2
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components.AddWordDialog
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components.FABDictionary
import com.bteamcoding.bubbletranslation.ui.theme.Inter
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterial3ScaffoldPaddingParameter")
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
//  viewModel: DictionaryViewModel = hiltViewModel(),
) {
    val viewModel: DictionaryViewModel2 = viewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var isDragging by remember { mutableStateOf(false) }

    lateinit var tts: TextToSpeech
    val context = LocalContext.current
    // Khởi tạo TTS
    tts = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
//                tts.setLanguage(Locale("vi", "VN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    LaunchedEffect(Unit) {
        if (sourceText != null) {
            viewModel.onAction(DictionaryAction.UpdateQuery(sourceText))
            viewModel.onAction(DictionaryAction.Search(sourceText))
        }
    }
//    LaunchedEffect(Unit) {
//        viewModel.onAction(DictionaryAction.OnLoadAllFolders)
//    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
    ) {
        Surface (
            modifier = Modifier.fillMaxSize(),
//            floatingActionButton = {
//                FABDictionary(
//                    onAddFolder = {
//                        viewModel.onAction(DictionaryAction.OnShowAddFolder)
//                    },
//                    onAddWord = {
//                        viewModel.onAction(DictionaryAction.OnShowAddWord)
//                    },
//                )
//            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                when {
                    state.definitions.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                12.dp
                            )
                        ) {
                            items(state.definitions) { entry ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .background(colorResource(R.color.purple_light))
                                            .padding(
                                                top = 8.dp,
                                                bottom = 12.dp,
                                                start = 16.dp,
                                                end = 16.dp
                                            )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Box(modifier = Modifier.padding(end = 8.dp)) {
                                                Text(
                                                    text = "${entry.english} [${entry.phonetic}]",
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    tts.speak(
                                                        entry.english,
                                                        TextToSpeech.QUEUE_FLUSH,
                                                        null,
                                                        null
                                                    )
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.speaker),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = colorResource(R.color.grey_medium)
                                                )
                                            }
                                            IconButton(
                                                onClick = {},
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Save,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(28.dp),
                                                    tint = if (state.isSavedWord)
                                                        colorResource(R.color.blue_dark)
                                                    else
                                                        colorResource(R.color.grey_medium)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Part of speech: ${entry.part_of_speech}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        entry.meanings.forEachIndexed { idx, meaning ->
                                            Text(
                                                text = "${idx + 1}. ${meaning.meaning}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = "→ ${meaning.vietnamese}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = colorResource(R.color.purple_dark)
                                            )
                                            Text(
                                                text = "Example: ${meaning.example_sentence}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "Dịch: ${meaning.vietnamese_translation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                                .padding(bottom = 240.dp),
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.error != null -> {
                        Text(
                            text = state.error ?: "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }


//                DictionaryScreen(
//                    state = state,
//                    onAction = { action -> viewModel.onAction(action) },
//                    onNavToAuthScreen = {},
//                    onDismiss = {
//                        viewModel.onAction(DictionaryAction.OnHideAddFolder)
//                        viewModel.onAction(DictionaryAction.OnHideAddWord)
//                    },
//                    onShowAddFolder = {
//                        viewModel.onAction(DictionaryAction.OnShowAddFolder)
//                    },
//                    onFolderNameChanged = {
//                        viewModel.onAction(DictionaryAction.OnFolderNameChanged(it))
//                    },
//                    onDismissError = {
//                        viewModel.onAction(DictionaryAction.ClearError)
//                    },
//                    onAddFolder = {
//                        viewModel.onAction(DictionaryAction.OnAddNewFolder)
//                    },
//                    onAddWord = {
//                        viewModel.onAction(DictionaryAction.OnAddNewWord(it))
//                    },
//                    onShowAddWord = {
//                        viewModel.onAction(DictionaryAction.OnShowAddWord)
//                    },
//                    onBack = {},
//                    searchQuery = sourceText
//                )
                // Divider
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun BottomSheetOnScreenPreview() {
//    BottomSheetOnScreen(
//        onDismissRequest = { /* handle dismiss */ },
//        textStyle = MaterialTheme.typography.titleMedium,
//        itemPadding = 10,
//        backgroundColor = MaterialTheme.colorScheme.surface,
//        sourceText = "Hello, world!",
//        onTap = { /* handle tap */ },
//        viewModel = ,
//    )
//}
