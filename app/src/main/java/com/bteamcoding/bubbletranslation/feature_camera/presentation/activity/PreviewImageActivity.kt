package com.bteamcoding.bubbletranslation.feature_camera.presentation.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.presentation.BaseActivity
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.core.utils.translateVisionText
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageAction
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageState
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageViewModel
import com.bteamcoding.bubbletranslation.feature_camera.presentation.component.TextOverlayOnImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PreviewImageActivity : BaseActivity() {
    private lateinit var viewModel: PreviewImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        supportActionBar?.hide() // Ẩn ActionBar

        setContent {
            viewModel = viewModel()
            val state: PreviewImageState by viewModel.state.collectAsStateWithLifecycle()

            val imageUriString = intent.getStringExtra("imageUri")
            val capturedImageUriString = intent.getStringExtra("capturedImageUri")
            val imageUri = remember { imageUriString?.let { Uri.parse(it) } }
            val capturedImageUri = remember { capturedImageUriString?.let { Uri.parse(it) } }

            var painter: AsyncImagePainter? by remember { mutableStateOf(null) }
            var bitmapToProcess: Bitmap? by remember { mutableStateOf(null) }

            //Use Coil to display the selected image
            if (capturedImageUri != null) {
                val file = File(capturedImageUri.path ?: "")
                painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(file)
                        .crossfade(true)
                        .allowHardware(false)
                        .build()
                )
            } else {
                painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = imageUri)
                        .crossfade(true)
                        .allowHardware(false)
                        .build()
                )
            }


            LaunchedEffect(painter?.state) {
                val painterState = painter?.state
                if (painterState is AsyncImagePainter.State.Success) {
                    val drawable = painterState.result.drawable
                    drawable.let {
                        val bitmap = it.toBitmap()
                        if (bitmap != bitmapToProcess) {
                            bitmapToProcess = bitmap
                        }
                    }
                }
            }

            // Chỉ gọi OCR khi bitmapToProcess thay đổi
            LaunchedEffect(bitmapToProcess) {
                bitmapToProcess?.let { bitmap ->
                    // gọi OCR async ở đây, ví dụ gọi viewmodel hoặc launch coroutine
                    viewModel.onAction(PreviewImageAction.SetImageBitmap(bitmap))
                    processBitmap(bitmap)
                }
            }


            PreviewImageView(
                painter = painter,
                visionText = state.translatedVisionText,
                bitmap = state.imageBitmap,
                isTextVisibility = state.isTextVisibility,
                onBack = {
                    viewModel.onAction(PreviewImageAction.OnReset)
                    finish()
                },
                onViewResultText = {

                },
                onCompareResult = {
                    viewModel.onAction(PreviewImageAction.OnChangeTextVisibility(it))
                }
            )
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = recognizeTextFromImage(bitmap)
                Log.d("PreviewImageActivity OCR", "Detected text: ${result.text}")
                val translatedResult = translateVisionText(result)
                viewModel.onAction(PreviewImageAction.OnChange(result))
                viewModel.onAction(PreviewImageAction.OnChangeTranslatedVisionText(translatedResult))
                viewModel.onAction(PreviewImageAction.OnChangeTextVisibility(true))
            } catch (e: Exception) {
                Log.e("PreviewImageActivity OCR", "Error: ${e.message}")
            }
        }
    }
}

@Composable
fun PreviewImageView(
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
                onClick = onViewResultText,
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
                                onCompareResult(false)
                                try {
                                    awaitRelease()
                                    onCompareResult(true)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                )
            }
        }



        if (painter != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { imageSize = it },
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )

                if (bitmap != null && visionText != null && isTextVisibility) {
                    val originalImageSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())

                    TextOverlayOnImage(
                        visionText = visionText,
                        imageSize = imageSize,
                        originalImageSize = originalImageSize
                    )
                } else if (visionText == null) {
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
    }
}
