package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FABMain

@Composable
fun BookmarkScreenRoot(
    viewModel: BookmarkViewModel = hiltViewModel(),
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
        onAddFolder = {

        },
        onSync = {

        }
    )
}

@Composable
fun BookmarkScreen(
    state: BookmarkState,
    onAddFolder: () -> Unit,
    onSync: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FABMain(
                onSync = onSync,
                onAddFolder = onAddFolder
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}