package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes
import com.bteamcoding.bubbletranslation.core.components.TopBar
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.CreateFolderDialog
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FABMain
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FolderItem

@Composable
fun BookmarkScreenRoot(
    viewModel: BookmarkViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.currentFolder) {
        if (state.currentFolder == null) {
            viewModel.onAction(BookmarkAction.OnLoadAllFolders)
        } else {
            viewModel.onAction(BookmarkAction.OnClearFolders)
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
    onAddFolder: () -> Unit,
    onSync: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    onReload: () -> Unit,
    onDismiss: () -> Unit,
    onFolderNameChanged: (String) -> Unit,
    onShowAddFolder: () -> Unit,
    onDismissError: () -> Unit
) {
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
                label = { Text("Search folder") },
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

            Text(
                modifier = Modifier
                    .constrainAs(textList) {
                        top.linkTo(searchBar.bottom, margin = 16.dp)
                    }
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    ),
                textAlign = TextAlign.Left,
                text = if (state.currentFolder == null) "Danh sách bộ thẻ" else "Danh sách từ vựng",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

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
                itemsIndexed(state.folders) { _, folder ->
                    FolderItem(folder = folder, onFolderClick = onFolderClick,
                        onEditClick = {}, onDeleteClick = {})
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

        if (!state.errorMessage.isNullOrEmpty()) {
            AlertDialog(
                onDismissRequest = {
                    // Đóng dialog và clear lỗi
                    onDismissError()
                },
                title = {
                    Text(text = "Đăng nhập thất bại", fontWeight = FontWeight.Bold)
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