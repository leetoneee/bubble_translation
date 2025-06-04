package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

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
        modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(topBar.bottom)
                    height = Dimension.fillToConstraints
                }
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(DictionaryAction.UpdateQuery(it)) },
                label = { Text("Search word") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAction(DictionaryAction.Search(state.searchQuery)) },
                enabled = !state.isLoading && state.searchQuery.isNotBlank()
            ) {
                Text("Tra cứu")
            }
            when {
                state.definitions.isNotEmpty() -> {
                    Text("${state.definitions}")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(state.error ?: "", color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}
