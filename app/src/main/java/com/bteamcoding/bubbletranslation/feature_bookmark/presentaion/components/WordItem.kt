package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme

@Composable
fun WordItem(
    word: Word,
    onWordClick: (Word) -> Unit,
    onDeleteClick: (Word) -> Unit,
    isOptionsRevealed: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    SwipeableItemWithActions(
        isRevealed = isOptionsRevealed,
        onExpanded = onExpanded,
        onCollapsed = onCollapsed,
        actions = {
            ActionIcon(
                onClick = { onDeleteClick(word) },
                backgroundColor = Color(0xFFF44336),
                icon = Icons.Default.Delete,
                contentDescription = "Delete Folder"
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7F7F7), RoundedCornerShape(16.dp))
                .clickable(
                    indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                    interactionSource = interactionSource
                ) {
                    onWordClick(word)
                }
                .padding(16.dp)
        ) {
            val (folderIcon, title, nextIcon, editIcon, deleteIcon) = createRefs()

            Box(
                modifier = Modifier
                    .constrainAs(folderIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .background(
                        color = colorResource(R.color.b_blue).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = "Word Icon",
                    modifier = Modifier.size(16.dp),
                    tint = colorResource(R.color.b_blue)
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(folderIcon.end, margin = 16.dp)
                    },
                text = word.word,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go to edit",
                modifier = Modifier
                    .constrainAs(nextIcon) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(20.dp)
            )
        }
    }
}

@Composable
@Preview
fun WordItemPreview() {
    BubbleTranslationTheme {
        WordItem(
            word = Word(id = "1", folderId = "", word = "Hello", deleted = false, updatedAt = 1),
            onDeleteClick = {},
            isOptionsRevealed = false,
            onExpanded = {},
            onCollapsed = {},
            onWordClick = {}
        )
    }
}