package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.components.SelectLang
import com.bteamcoding.bubbletranslation.core.components.TopBar
import com.bteamcoding.bubbletranslation.feature_camera.presentation.activity.CameraScreenActivity
import com.bteamcoding.bubbletranslation.feature_camera.presentation.activity.PreviewImageActivity
import com.bteamcoding.bubbletranslation.ui.theme.Inter

@Composable
fun CameraScreenRoot(
    viewModel: CameraScreenViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CameraScreen(
        onCameraMode = {
            val intent = Intent(context, CameraScreenActivity::class.java)
            context.startActivity(intent)
        },
        onImageChosen = {
            viewModel.onAction(CameraScreenAction.SetUri(it))
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putExtra("imageUri", it.toString())
            context.startActivity(intent)
        }
    )
}

@Composable
fun CameraScreen(
    onCameraMode: () -> Unit,
    onImageChosen: (Uri) -> Unit
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

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topBar, image, controller) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
        ) {
            TopBar("Image Recognition")
        }

        Column(
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(topBar.bottom)
                    bottom.linkTo(controller.top)
                    height = Dimension.fillToConstraints
                }
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = colorResource(R.color.blue_icon),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(R.drawable.bee_plain),
//                    contentDescription = null,
//                    modifier = Modifier.size(80.dp),
//                    tint = colorResource(R.color.blue_900)
//                )
                Image(
                    painter = painterResource(R.drawable.bee_plain),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Text(text = "Take a picture or\nChoose image from library", fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, lineHeight = 20.sp, color = colorResource(R.color.bold_text), modifier = Modifier.padding(top = 30.dp, bottom = 10.dp))
            Text(text = "Text in the image will be translated\n and displayed here", fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center, color = colorResource(R.color.grey_light))
        }

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .background(
                    color = colorResource(R.color.blue_lightest),
                    shape = RoundedCornerShape(26.dp)
                )
                .constrainAs(controller) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 26.dp)
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .width(144.dp)
                    .wrapContentHeight()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

            ){
                SelectLang(shapeSize = 40.dp, textSize = 14.sp)
            }

            VerticalDivider(
                modifier = Modifier.padding(vertical = 8.dp).height(32.dp),
                thickness = 3.dp,
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .width(100.dp)
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                OutlinedIconButton(
                    onClick = onCameraMode,
                    border = BorderStroke(2.4.dp, color = colorResource(R.color.blue_dark)),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.b_gray))
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = colorResource(R.color.blue_dark)
                    )
                }
                OutlinedIconButton(
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
                    border = BorderStroke(2.4.dp, color = colorResource(R.color.blue_dark)),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.b_gray))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = colorResource(R.color.blue_dark)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(backgroundColor = 0x000000)
fun CameraScreenPre() {
    CameraScreen(
        onCameraMode = {},
        onImageChosen = {}
    )
}