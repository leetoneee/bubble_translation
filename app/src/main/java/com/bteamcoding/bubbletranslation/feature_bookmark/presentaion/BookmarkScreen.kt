package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.navigation.DictionaryScreenParams
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes
import com.bteamcoding.bubbletranslation.core.components.TopBar
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.ConfirmDeleteDialog
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.ConfirmDeleteWordDialog
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.CreateFolderDialog
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FABMain
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FolderItem
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FolderItemUI
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.UpdateFolderDialog
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.WordItem
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.WordItemUI

@Composable
fun BookmarkScreenRoot(
    viewModel: BookmarkViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(BookmarkAction.OnLoadCurrentUser)
    }

    LaunchedEffect(state.currentFolder) {
        if (state.currentFolder == null) {
            viewModel.onAction(BookmarkAction.OnLoadAllFolders)
        } else {
            viewModel.onAction(BookmarkAction.OnClearFolders)
            viewModel.onAction(BookmarkAction.OnLoadWordsByFolder(state.currentFolder!!.id))
        }
    }

    BookmarkScreen(
        state = state,
        onNavToAuthScreen = {
            navController.navigate(NavRoutes.AUTH)
        },
        onQueryChanged = {
            viewModel.onAction(BookmarkAction.OnQueryChanged(it))
        },
        onClearSearchQuery = {
            viewModel.onAction(BookmarkAction.OnQueryChanged(""))
        },
        onAddFolder = {
            viewModel.onAction(BookmarkAction.OnAddNewFolder)
        },
        onSync = {
            viewModel.onAction(BookmarkAction.OnSync)
        },
        onFolderClick = {
            viewModel.onAction(BookmarkAction.OnFolderClick(it))
        },
        onReload = {
            viewModel.onAction(BookmarkAction.OnLoadAllFolders)
        },
        onDismiss = {
            viewModel.onAction(BookmarkAction.OnHideAddFolder)
        },
        onShowAddFolder = {
            viewModel.onAction(BookmarkAction.OnShowAddFolder)
        },
        onFolderNameChanged = {
            viewModel.onAction(BookmarkAction.OnFolderNameChanged(it))
        },
        onDismissError = {
            viewModel.onAction(BookmarkAction.ClearError)
        },
        onUpdateFolder = {
            viewModel.onAction(BookmarkAction.OnUpdateFolderName)
        },
        onShowEditFolder = {
            viewModel.onAction(BookmarkAction.OnShowEditFolder(it))
        },
        onDismissEditFolder = {
            viewModel.onAction(BookmarkAction.OnHideEditFolder)
        },
        onDeleteFolder = {
            viewModel.onAction(BookmarkAction.OnDeleteFolder)
        },
        onShowConfirm = {
            viewModel.onAction(BookmarkAction.OnShowConfirm(it))
        },
        onDismissConfirm = {
            viewModel.onAction(BookmarkAction.OnHideConfirm)
            viewModel.onAction(BookmarkAction.OnHideConfirmDeleteWord)
        },
        onDeleteWord = {
            viewModel.onAction(BookmarkAction.OnDeleteWord)
        },
        onShowConfirmDeleteWord = {
            viewModel.onAction(BookmarkAction.OnShowConfirmDeleteWord(it))
        },
        onWordClick = {
            navController.navigate(DictionaryScreenParams(it.word))
        },
        onSuccess = {
            viewModel.onAction(BookmarkAction.OnSuccess)
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookmarkScreen(
    state: BookmarkState,
    onNavToAuthScreen: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onSync: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    onWordClick: (Word) -> Unit,
    onReload: () -> Unit,
    onDismiss: () -> Unit,
    onFolderNameChanged: (String) -> Unit,
    onAddFolder: () -> Unit,
    onShowAddFolder: () -> Unit,
    onUpdateFolder: () -> Unit,
    onShowEditFolder: (Folder) -> Unit,
    onDismissEditFolder: () -> Unit,
    onDeleteFolder: () -> Unit,
    onShowConfirm: (Folder) -> Unit,
    onDismissConfirm: () -> Unit,
    onDismissError: () -> Unit,
    onDeleteWord: () -> Unit,
    onShowConfirmDeleteWord: (Word) -> Unit,
    onSuccess: () -> Unit
) {
    val folderStates = remember(state.folders) {
        state.folders.map { FolderItemUI(it, false) }.toMutableStateList()
    }

    val wordStates = remember(state.words) {
        state.words.map { WordItemUI(it, false) }.toMutableStateList()
    }

    var hasInitializedFolderName by remember { mutableStateOf(false) }

    if (!state.showEditFolderDialog) {
        hasInitializedFolderName = false // reset lại khi đóng dialog
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FABMain(
                onSync = onSync,
                onAddFolder = onShowAddFolder,
                onReload = onReload
            )
        }
    ) {
        val focusManager = LocalFocusManager.current

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // Bỏ focus khi người dùng chạm ra ngoài trường tìm kiếm
                            focusManager.clearFocus()
                        }
                    )
                },
        ) {
            val (topBar, searchBar, textList, list) = createRefs()

            Box(
                modifier = Modifier
                    .constrainAs(topBar) {
                        top.linkTo(parent.top)
                    }
                    .fillMaxWidth()
            ) {
                TopBar("Bookmark", onNavToAuthScreen = onNavToAuthScreen)
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onQueryChanged,
                label = {
                    Text(
                        text = if (state.currentFolder != null)
                            "Search word"
                        else
                            "Search folder"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    autoCorrectEnabled = true
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearchQuery) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(searchBar) {
                        top.linkTo(topBar.bottom, margin = 8.dp)
                    }
                    .padding(
                        horizontal = 16.dp
                    )
            )

            Row(
                modifier = Modifier
                    .constrainAs(textList) {
                        top.linkTo(searchBar.bottom, margin = 16.dp)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon back
                if (state.currentFolder != null) {
                    FilledIconButton(
                        onClick = onReload,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.Transparent
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text
                val labelText = if (state.currentFolder == null) {
                    AnnotatedString("Danh sách bộ thẻ")
                } else {
                    buildAnnotatedString {
                        append("Danh sách từ vựng của ")
                        withStyle(style = SpanStyle(color = colorResource(R.color.blue_dark))) {
                            append(state.currentFolder.name)
                        }
                    }
                }

                Text(
                    text = labelText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            }

            if (state.currentFolder == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(list) {
                            top.linkTo(textList.bottom, margin = 16.dp)
                        }
                        .padding(
                            horizontal = 16.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(folderStates) { _, folderState ->
                        FolderItem(
                            folder = folderState.folder,
                            onFolderClick = onFolderClick,
                            onEditClick = { onShowEditFolder(it) },
                            onDeleteClick = { onShowConfirm(it) },
                            isOptionsRevealed = folderState.isOptionsRevealed,
                            onExpanded = {
                                val index =
                                    folderStates.indexOfFirst { it.folder.id == folderState.folder.id }
                                if (index != -1) {
                                    folderStates[index] = folderState.copy(isOptionsRevealed = true)
                                }
                            },
                            onCollapsed = {
                                val index =
                                    folderStates.indexOfFirst { it.folder.id == folderState.folder.id }
                                if (index != -1) {
                                    folderStates[index] =
                                        folderState.copy(isOptionsRevealed = false)
                                }
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(list) {
                            top.linkTo(textList.bottom, margin = 16.dp)
                        }
                        .padding(
                            horizontal = 16.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(wordStates) { _, wordState ->
                        WordItem(
                            onDeleteClick = {
                                onShowConfirmDeleteWord(it)
                            },
                            isOptionsRevealed = wordState.isOptionsRevealed,
                            onExpanded = {
                                val index =
                                    wordStates.indexOfFirst { it.word.id == wordState.word.id }
                                if (index != -1) {
                                    wordStates[index] = wordState.copy(isOptionsRevealed = true)
                                }
                            },
                            onCollapsed = {
                                val index =
                                    wordStates.indexOfFirst { it.word.id == wordState.word.id }
                                if (index != -1) {
                                    wordStates[index] = wordState.copy(isOptionsRevealed = false)
                                }
                            },
                            word = wordState.word,
                            onWordClick = {
                                onWordClick(it)
                            }
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

        LaunchedEffect(state.showEditFolderDialog) {
            if (state.showEditFolderDialog && !hasInitializedFolderName && state.tempFolder != null) {
                onFolderNameChanged(state.tempFolder.name)
                hasInitializedFolderName = true
            }
        }

        if (state.showEditFolderDialog && state.tempFolder != null) {
            UpdateFolderDialog(
                folderName = state.folderName,
                onFolderNameChanged = onFolderNameChanged,
                onConfirm = {
                    onUpdateFolder()
                },
                onDismiss = onDismissEditFolder
            )
        }

        if (state.showConfirmDialog && state.tempFolder != null) {
            ConfirmDeleteDialog(
                folder = state.tempFolder,
                onConfirm = onDeleteFolder,
                onDismiss = onDismissConfirm
            )
        }

        if (state.showConfirmDeleteWordDialog && state.tempWord != null) {
            ConfirmDeleteWordDialog(
                word = state.tempWord,
                onConfirm = onDeleteWord,
                onDismiss = onDismissConfirm
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

        if (!state.successMessage.isNullOrEmpty()) {
            AlertDialog(
                onDismissRequest = onSuccess,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50), // màu xanh lá
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.successMessage,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = onSuccess) {
                        Text("OK", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0D12).copy(alpha = 0.4f))
                    .zIndex(30f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}