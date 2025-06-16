package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.ui.theme.BubbleTranslationTheme
import kotlin.math.roundToInt

@Composable
fun FolderItem(
    folder: Folder,
    onFolderClick: (Folder) -> Unit,
    onEditClick: (Folder) -> Unit,
    onDeleteClick: (Folder) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F7F7), RoundedCornerShape(16.dp))
            .clickable(
                indication = rememberRipple(bounded = true), // Ripple hiệu ứng khi nhấn
                interactionSource = interactionSource
            ) {
                onFolderClick(folder)
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
                    color = colorResource(R.color.b_orange).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Folder Icon",
                modifier = Modifier.size(16.dp),
                tint = colorResource(R.color.b_orange)
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(folderIcon.end, margin = 16.dp)
                },
            text = folder.name,
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

//@Composable
//fun FolderItem(
//    folder: Folder,
//    onFolderClick: (Folder) -> Unit,
//    onEditClick: (Folder) -> Unit,
//    onDeleteClick: (Folder) -> Unit
//) {
//    val draggableState = rememberDraggableState { delta ->
//        // Track the drag offset (delta) here
//    }
//
//    val swipeThreshold = with(LocalDensity.current) { 200.dp.toPx() } // Drag threshold to show actions
//
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//            .background(Color(0xFFF7F7F7), RoundedCornerShape(16.dp))
//            .clickable { onFolderClick(folder) }  // Click to open the folder
//            .padding(16.dp)
//            .draggable(
//                state = draggableState,
//                orientation = Orientation.Horizontal,  // Horizontal drag
//                onDragStarted = {
//                    // Optionally handle when drag starts
//                },
//                onDragStopped = { velocity ->
//                    // Handle drag end
//                    if (draggableState.offset.value < -swipeThreshold) {
//                        // Show edit and delete buttons if dragged past threshold
//                    }
//                }
//            )
//    ) {
//        val (folderIcon, title, nextIcon, editIcon, deleteIcon) = createRefs()
//
//        // Folder Icon
//        Box(
//            modifier = Modifier
//                .constrainAs(folderIcon) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                }
//                .background(
//                    color = colorResource(R.color.b_orange).copy(alpha = 0.3f),
//                    shape = RoundedCornerShape(8.dp)
//                )
//                .padding(4.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.Folder,
//                contentDescription = "Folder Icon",
//                modifier = Modifier.size(16.dp),
//                tint = colorResource(R.color.b_orange)
//            )
//        }
//
//        // Folder Name Text
//        Text(
//            modifier = Modifier
//                .constrainAs(title) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(folderIcon.end, margin = 16.dp)
//                },
//            text = folder.name,
//            fontWeight = FontWeight.SemiBold,
//            fontSize = 16.sp,
//        )
//
//        // Chevron Icon (to indicate something like "Go to edit")
//        Icon(
//            imageVector = Icons.Default.ChevronRight,
//            contentDescription = "Go to edit",
//            modifier = Modifier
//                .constrainAs(nextIcon) {
//                    end.linkTo(parent.end)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                }
//                .size(20.dp)
//        )
//
//        // Show edit and delete icons when dragged past threshold
//        AnimatedVisibility(visible = draggableState.offset.value < 0) {
//            Row(
//                modifier = Modifier
//                    .constrainAs(editIcon) {
//                        end.linkTo(parent.end)
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    }
//                    .background(Color.Green)
//                    .padding(16.dp)
//                    .clickable { onEditClick(folder) },  // Click to edit the folder
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
//            }
//
//            Row(
//                modifier = Modifier
//                    .constrainAs(deleteIcon) {
//                        end.linkTo(parent.end)
//                        top.linkTo(editIcon.bottom)
//                    }
//                    .background(Color.Red)
//                    .padding(16.dp)
//                    .clickable { onDeleteClick(folder) },  // Click to delete the folder
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
//            }
//        }
//    }
//}



@Composable
@Preview
fun FolderItemPreview() {
    BubbleTranslationTheme {
        FolderItem(
            folder = Folder(id = "1", updatedAt = 1, name = "Tech", deleted = false),
            onFolderClick = {},
            onDeleteClick = {},
            onEditClick = {}
        )
    }
}