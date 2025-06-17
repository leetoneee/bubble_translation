package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeDefaults.Spacing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bteamcoding.bubbletranslation.core.components.TopBar
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.ui.theme.Inter
import kotlinx.coroutines.delay
import java.util.Locale
import androidx.navigation.NavController
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.CreateFolderDialog
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components.AddWordDialog
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components.FABDictionary

@Composable
fun DictionaryScreenRoot(
    viewModel: DictionaryViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(DictionaryAction.OnLoadAllFolders)
    }

    DictionaryScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        onNavToAuthScreen = {
            navController.navigate(NavRoutes.AUTH)
        },
        onDismiss = {
            viewModel.onAction(DictionaryAction.OnHideAddFolder)
            viewModel.onAction(DictionaryAction.OnHideAddWord)
        },
        onShowAddFolder = {
            viewModel.onAction(DictionaryAction.OnShowAddFolder)
        },
        onFolderNameChanged = {
            viewModel.onAction(DictionaryAction.OnFolderNameChanged(it))
        },
        onDismissError = {
            viewModel.onAction(DictionaryAction.ClearError)
        },
        onAddFolder = {
            viewModel.onAction(DictionaryAction.OnAddNewFolder)
        },
        onAddWord = {
            viewModel.onAction(DictionaryAction.OnAddNewWord(it))
        },
        onShowAddWord = {
            viewModel.onAction(DictionaryAction.OnShowAddWord)
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DictionaryScreen(
    state: DictionaryScreenState,
    onAction: (DictionaryAction) -> Unit,
    onNavToAuthScreen: () -> Unit,
    onDismiss: () -> Unit,
    onFolderNameChanged: (String) -> Unit,
    onAddWord: (Folder) -> Unit,
    onAddFolder: () -> Unit,
    onShowAddWord: () -> Unit,
    onShowAddFolder: () -> Unit,
    onDismissError: () -> Unit
) {
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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FABDictionary(
                onAddFolder = onShowAddFolder,
                onAddWord = onShowAddWord
            )
        }
    ) {
        val focusManager = LocalFocusManager.current

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // Bỏ focus khi người dùng chạm ra ngoài trường tìm kiếm
                            focusManager.clearFocus()
                        }
                    )
                },
        ) {
            val (topBar, content) = createRefs()

            Box(
                modifier = Modifier
                    .constrainAs(topBar) {
                        top.linkTo(parent.top)
                    }
                    .fillMaxWidth()
            ) {
                TopBar("Dictionary Lookup", onNavToAuthScreen = onNavToAuthScreen)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(topBar.bottom)
                    }
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 60.dp
                    )
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    // Add a short delay to allow the UI to compose before requesting focus
                    delay(100)
                    focusRequester.requestFocus()
                }
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(DictionaryAction.UpdateQuery(it)) },
                    label = { Text("Search word") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        autoCorrectEnabled = true
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onAction(DictionaryAction.Search(state.searchQuery))
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon"
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                onAction(DictionaryAction.ClearSearch)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear text"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                when {
                    !state.isLoading && state.definitions.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 178.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = colorResource(R.color.purple_light),
                                        shape = RoundedCornerShape(30.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.bee_pink_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                            Text(
                                text = "Enter the English word\nyou want to look up",
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                color = colorResource(R.color.bold_text),
                                modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
                            )
                            Text(
                                text = "Its meaning and example sentences\nwill be displayed here",
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.grey_light)
                            )
                        }
                    }

                    state.definitions.isNotEmpty() -> {
                        LaunchedEffect(state.definitions) {
                            keyboardController?.hide()
                        }
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
            }
        }

        if (state.showAddFolderDialog) {
            CreateFolderDialog(
                onConfirm = onAddFolder,
                onDismiss = onDismiss,
                folderName = state.folderName,
                onFolderNameChanged = onFolderNameChanged
            )
        }

        if (state.showAddWordDialog) {
            AddWordDialog(
                onConfirm = {
                    onAddWord(it)
                },
                onDismiss = onDismiss,
                word = state.searchQuery,
                folders = state.folders
            )
        }

        if (!state.errorMessage.isNullOrEmpty()) {
            AlertDialog(
                onDismissRequest = {
                    // Đóng dialog và clear lỗi
                    onDismissError()
                },
                title = {
                    Text(text = "Lỗi", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(text = state.errorMessage ?: "")
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Clear lỗi khi đóng dialog
                        onDismissError()
                    }) {
                        Text("OK")
                    }
                },
                icon = {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                },
                containerColor = Color.White,
                titleContentColor = Color.Black,
                textContentColor = Color.DarkGray
            )
        }

    }
}

