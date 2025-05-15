package com.bteamcoding.bubbletranslation.feature_camera.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bteamcoding.bubbletranslation.app.presentation.BaseActivity
import com.bteamcoding.bubbletranslation.feature_camera.presentation.CameraScreenAction
import com.bteamcoding.bubbletranslation.feature_camera.presentation.CameraScreenViewModel
import com.bteamcoding.bubbletranslation.feature_camera.presentation.component.CameraPreview

class CameraScreenActivity : BaseActivity() {
    private lateinit var viewModel: CameraScreenViewModel
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        supportActionBar?.hide() // Ẩn ActionBar

//        WindowCompat.setDecorFitsSystemWindows(window, false) // Full màn hình
        setContent {
            viewModel = viewModel()
            val context = LocalContext.current

            val controller = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(
                        CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
                    )
                }
            }

            CameraScreenView(
                controller = controller,
                onSwitchCamera = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                onCaptureImage = {

                },
                onBack = {
                    finish()
                },
                onImageChosen = {
                    viewModel.onAction(CameraScreenAction.SetUri(it))
                    val intent = Intent(context, PreviewImageActivity::class.java)
                    intent.putExtra("imageUri", it.toString())
                    context.startActivity(intent)
                }
            )
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

@Composable
fun CameraScreenView(
    controller: LifecycleCameraController,
    onSwitchCamera: () -> Unit,
    onCaptureImage: () -> Unit,
    onBack: () -> Unit,
    onImageChosen: (Uri) -> Unit,
) {
    //The URI of the photo that the user has picked
    var photoUri: Uri? by remember { mutableStateOf(null) }

    //The launcher we will use for the PickVisualMedia contract.
    //When .launch()ed, this will display the photo picker.
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            //When the user has selected a photo, its URI is returned here
            if (uri != null) {
                photoUri = uri
                onImageChosen(uri) // gọi trực tiếp
            }
        }

    LaunchedEffect(photoUri) {
        photoUri?.let { onImageChosen(it) }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topGrp, bottomGrp) = createRefs()

        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        ConstraintLayout(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .constrainAs(topGrp) {
                    top.linkTo(parent.top)
                }
        ) {
            val (backBtn, transGrp, libsBtn) = createRefs()

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
                    //On button press, launch the photo picker
                    launcher.launch(
                        PickVisualMediaRequest(
                            //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
                            //Or use .VideoOnly if you only want videos.
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF0A0D12).copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(libsBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Choose image",
                    tint = Color.White
                )
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .padding(bottom = 96.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .constrainAs(bottomGrp) {
                    bottom.linkTo(parent.bottom)
                }
        ) {
            val (switchBtn, captureBtn) = createRefs()

            FilledIconButton(
                onClick = onSwitchCamera,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF0A0D12).copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(switchBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White
                )
            }

            OutlinedIconButton(
                onClick = onCaptureImage,
                border = BorderStroke(3.dp, Color.White),
                modifier = Modifier
                    .size(70.dp)
                    .constrainAs(captureBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = Color.White, shape = CircleShape)
                )
            }
        }
    }
}