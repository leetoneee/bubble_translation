package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DictionaryScreenRoot(
    viewModel: DictionaryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DictionaryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DictionaryScreen(
    state: DictionaryScreenState,
    onAction: (DictionaryAction) -> Unit
) {
    Column {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onAction(DictionaryAction.Search(it)) },
            label = { Text("Search word") },
            modifier = Modifier.fillMaxWidth()
        )
        // Add more UI elements here as needed
    }
}
