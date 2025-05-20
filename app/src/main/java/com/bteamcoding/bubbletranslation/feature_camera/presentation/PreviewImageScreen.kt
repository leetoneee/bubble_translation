package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImagePainter
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.bteamcoding.bubbletranslation.feature_camera.presentation.component.TextOverlayOnImage

@Composable
fun PreviewImageScreen(
    painter: AsyncImagePainter?,
    bitmap: Bitmap?,
    visionText: TranslatedVisionText?,
    isTextVisibility: Boolean,
    onBack: () -> Unit,
    onViewResultText: () -> Unit,
    onCompareResult: (Boolean) -> Unit
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.white_background))
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topGrp) = createRefs()

        ConstraintLayout(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .constrainAs(topGrp) {
                    top.linkTo(parent.top)
                }
                .zIndex(100f)
        ) {
            val (backBtn, viewResultBtn, compareBtn) = createRefs()

            FilledIconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF0A0D12).copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Switch camera",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            FilledIconButton(
                onClick = {
                    if (visionText != null) {
                        onViewResultText()
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF0A0D12).copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(viewResultBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(compareBtn.start, margin = 8.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = "Text",
                    tint = Color.White
                )
            }

            FilledIconButton(
                onClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF0A0D12).copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(compareBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Compare,
                    contentDescription = "Compare",
                    tint = Color.White,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (visionText != null) {
                                    onCompareResult(false)
                                    try {
                                        awaitRelease()
                                        onCompareResult(true)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }

        if (painter != null) {
            // Zoom in/out, Pinch
            var scale by remember {
                mutableFloatStateOf(1f)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val state = rememberTransformableState { zoomChange, panChange, _ ->
                    scale = (scale * zoomChange).coerceIn(1f, 5f)

                    val extraWidth = (scale - 1) * constraints.maxWidth
                    val extraHeight = (scale - 1) * constraints.maxHeight

                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2

                    offset = Offset(
                        x = (offset.x + panChange.x).coerceIn(-maxX, maxX),
                        y = (offset.y + panChange.y).coerceIn(-maxY, maxY)
                    )
                }

                val transformModifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }

                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { tapOffset ->
                                    val oldScale = scale
                                    val newScale = if (scale < 2f) 2f else 1f

                                    // Tính toán offset sao cho điểm tap không bị lệch
                                    val extraWidth = (newScale - 1) * constraints.maxWidth
                                    val extraHeight = (newScale - 1) * constraints.maxHeight
                                    val maxX = extraWidth / 2
                                    val maxY = extraHeight / 2

                                    val zoomCenter = tapOffset - Offset(
                                        constraints.maxWidth / 2f,
                                        constraints.maxHeight / 2f
                                    )
                                    val newOffset =
                                        (offset - zoomCenter) * (newScale / oldScale) + zoomCenter

                                    // Clamp lại trong giới hạn
                                    offset = Offset(
                                        x = newOffset.x.coerceIn(-maxX, maxX),
                                        y = newOffset.y.coerceIn(-maxY, maxY)
                                    )

                                    scale = newScale
                                }
                            )
                        }
                        .onSizeChanged { imageSize = it }
                        .align(Alignment.Center)
                        .then(transformModifier)
                        .transformable(state)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(10f),
                        contentScale = ContentScale.Fit
                    )

                    if (bitmap != null && visionText != null && visionText.text.isNotEmpty() && isTextVisibility) {
                        val originalImageSize =
                            Size(bitmap.width.toFloat(), bitmap.height.toFloat())

                        TextOverlayOnImage(
                            visionText = visionText,
                            imageSize = imageSize,
                            originalImageSize = originalImageSize
                        )
                    }
                }

                // Handling Loading / No Text states
                if (visionText == null) {
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
                } else if (visionText.text == "") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0A0D12).copy(alpha = 0.2f))
                            .zIndex(30f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .background(
                                    Color(0xFF0A0D12).copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "No text detected.",
                                color = Color.White,
                                fontWeight = FontWeight.Normal,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}