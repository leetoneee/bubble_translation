package com.bteamcoding.bubbletranslation.feature_dictionary.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FABItem
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components.FABItemUI

@Composable
fun FABDictionary(
    onAddFolder: () -> Unit,
    onAddWord: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val onToggleFAB = {
        expanded = !expanded
    }

    val items = listOf(
        FABItem(Icons.Filled.PostAdd, "Add word", {
            onAddWord()
            onToggleFAB()
        }),
        FABItem(Icons.Filled.CreateNewFolder, "Add folder", {
            onAddFolder()
            onToggleFAB()
        })
    )

    Column(horizontalAlignment = Alignment.End) {
        val transition = updateTransition(targetState = expanded, label = "transition")
        val rotation by transition.animateFloat(label = "rotation") {
            if (it) 315f else 0f
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically(),
        ) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items) { _, it ->
                    FABItemUI(it)
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(8.dp)
                .background(Color.Transparent)
        )

        FloatingActionButton(
            onClick = onToggleFAB,
            modifier = Modifier.rotate(rotation),
            containerColor = colorResource(R.color.blue_dark)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "", tint = Color.White)
        }
    }
}
