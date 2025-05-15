package com.bteamcoding.bubbletranslation.feature_camera.presentation.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.presentation.BaseActivity
import java.io.File

class PreviewImageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        supportActionBar?.hide() // Ẩn ActionBar

        setContent {
            val imageUriString = intent.getStringExtra("imageUri")
            val capturedImageUriString = intent.getStringExtra("capturedImageUri")
            val imageUri = remember { imageUriString?.let { Uri.parse(it) } }
            val capturedImageUri = remember { capturedImageUriString?.let { Uri.parse(it) } }

            PreviewImageView(
                imageUri = imageUri ?: capturedImageUri,
                isCapturedImage = capturedImageUri != null,
                onBack = {
                    finish()
                },
                onViewResultText = {

                },
                onCompareResult = {

                }
            )

        }
    }
}

@Composable
fun PreviewImageView(
    imageUri: Uri?,
    isCapturedImage: Boolean,
    onBack: () -> Unit,
    onViewResultText: () -> Unit,
    onCompareResult: () -> Unit
) {
//    Box(
//        modifier = Modifier.fillMaxSize().background(color = colorResource(R.color.white_background))
//    )

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
                onClick = onCompareResult,
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
                    tint = Color.White
                )
            }
        }

        if (imageUri != null) {
            //Use Coil to display the selected image
            var painter: AsyncImagePainter? = null

            if (isCapturedImage) {
                val file = File(imageUri.path ?: "")
                painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(file)
                        .crossfade(true)
                        .allowHardware(false)
                        .build()
                )
            } else
                painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = imageUri)
                        .crossfade(true)
                        .allowHardware(false)
                        .build()
                )

            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(50f),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            val progress by remember { mutableFloatStateOf(0.1f) }
            val animatedProgress by
            animateFloatAsState(
                targetValue = progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = { animatedProgress })
            }
        }
    }
}