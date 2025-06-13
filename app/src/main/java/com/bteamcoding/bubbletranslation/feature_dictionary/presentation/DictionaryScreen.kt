package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (topBar, content) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
        ) {
            TopBar("Dictionary Lookup")
        }

        Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
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
                modifier = Modifier.fillMaxWidth()
            )

            when {
                state.definitions.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 12.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.definitions) { entry ->
                            androidx.compose.material3.Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${entry.english} [${entry.phonetic}]",
                                        style = MaterialTheme.typography.titleMedium
                                    )
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
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Example: ${meaning.example_sentence}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Dịch: ${meaning.vietnamese_translation}",
                                            style = MaterialTheme.typography.bodySmall
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
}

