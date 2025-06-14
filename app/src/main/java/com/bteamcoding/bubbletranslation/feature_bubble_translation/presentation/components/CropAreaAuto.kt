package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AutoScreenModeState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import com.bteamcoding.bubbletranslation.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout

@SuppressLint("LocalContextConfigurationRead", "ConfigurationScreenWidthHeight")
@Composable
fun CropAreaAuto(
    captureRegionParam: Rect,
    onCaptureRegionChange: (Rect) -> Unit,
    onClear: () -> Unit,
    onCheck: () -> Unit,
    onChangeTextVisibility: (Boolean) -> Unit
) {
    var captureRegion by remember { mutableStateOf(captureRegionParam) }
    var resizeType by remember { mutableStateOf<ResizeType?>(null) }

    // Lấy chiều rộng và chiều cao màn hình từ Configuration
    val context = LocalContext.current
    val screenWidth = context.resources.configuration.screenWidthDp.dp // Chiều rộng màn hình
    val screenHeight = context.resources.configuration.screenHeightDp.dp  // Chiều cao màn hình
    Log.d("ScreenSize", "width: $screenWidth, height: $screenHeight")

    val optionBarHeight = 40

    var yOffset = if ((captureRegion.top.dp - optionBarHeight.dp) > 0.dp) {
        captureRegion.top.dp - optionBarHeight.dp
    } else {
        captureRegion.bottom.dp
    }

    val dfSize = 30.dp

    val borderColor = colorResource(id = R.color.second_primary)

    //Box 1: Lớp phủ trong suốt
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val alpha = 0.1f
                //nen trai
                drawCustomRect(
                    color = Color(0xFF000000).copy(alpha = alpha),
                    topLeft = Offset(0f, 0f),
                    size = Size(captureRegion.left * density, screenHeight.toPx()),
                )
                //nen phai
                drawCustomRect(
                    color = Color(0xFF000000).copy(alpha = alpha),
                    topLeft = Offset(captureRegion.right * density, 0f),
                    size = Size(
                        screenWidth.toPx() - captureRegion.right * density,
                        screenHeight.toPx()
                    ),
                )
                //nen tren
                drawCustomRect(
                    color = Color(0xFF000000).copy(alpha = alpha),
                    topLeft = Offset(captureRegion.left * density, 0f),
                    size = Size(
                        (captureRegion.right - captureRegion.left) * density,
                        captureRegion.top * density
                    ),
                )
                //nen duoi
                drawCustomRect(
                    color = Color(0xFF000000).copy(alpha = alpha),
                    topLeft = Offset(captureRegion.left * density, captureRegion.bottom * density),
                    size = Size(
                        (captureRegion.right - captureRegion.left) * density,
                        screenHeight.toPx() - captureRegion.bottom * density
                    ),
                )
            }

    ) {
        Row(
            modifier = Modifier
                .offset(
                    x = captureRegion.left.dp,
                    y = yOffset)
                .padding(8.dp), // khoảng cách từ mép box
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box (
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(4.dp)
                    .clickable {
                        onCheck()
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_check_24), // icon dấu tick
                    contentDescription = "Check",
                    tint = colorResource(id = R.color.second_primary),
                    modifier = Modifier.size(24.dp)
                )
            }
            Box (
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(4.dp)
                    .clickable {
                        onClear()
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_clear_24), // icon dấu X
                    contentDescription = "Close",
                    tint = colorResource(id = R.color.second_primary),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // Box 2: Hình chữ nhật có thể thay đổi kích thước và kéo
        Box(

            modifier = Modifier
                .offset(
                    (captureRegion.left).dp, // Sử dụng dp trực tiếp cho vị trí
                    (captureRegion.top).dp   // Sử dụng dp trực tiếp cho vị trí
                )
                .size(
                    (captureRegion.right - captureRegion.left).dp,  // Sử dụng dp cho chiều rộng
                    (captureRegion.bottom - captureRegion.top).dp  // Sử dụng dp cho chiều cao
                )
                .background(Color.Transparent)
                .clip(shape = RoundedCornerShape(6.dp))
                .drawBehind {
                    val radius = 7.dp.toPx()
                    val stroke = 5.dp.toPx()
                    val defaultSize = (dfSize).toPx()

                    //Top-left
                    drawCustomArc(
                        color = borderColor,
                        startAngle = 180f,
                        topLeft = Offset(0f, 0f),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                    //duong thang dung den goc top-left
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(0f, radius),
                        end = Offset(0f, defaultSize),
                        strokeWidth = stroke
                    )
                    //duong ngang tu goc top-left sang phai
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(radius, 0f),
                        end = Offset(defaultSize, 0f),
                        strokeWidth = stroke
                    )

                    //duong ngang chinh giua canh top
                    drawCustomLine(
                        color = borderColor,
                        start = Offset((size.width - defaultSize) / 2, 0f),
                        end = Offset((size.width + defaultSize) / 2, 0f),
                        strokeWidth = stroke
                    )

                    //Top-right
                    drawCustomArc(
                        color = borderColor,
                        startAngle = 270f, // bắt đầu từ trên (12h), vẽ qua phải (3h)
                        topLeft = Offset(
                            size.width - radius * 2,
                            0f
                        ), // dịch trái lại 2*radius từ cạnh phải
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                    // Đường ngang từ trái đến goc top-right
                    drawCustomLine(
                        color = borderColor,
                        start = Offset((size.width - defaultSize), 0f),
                        end = Offset(size.width - radius, 0f),
                        strokeWidth = stroke
                    )
                    // Đường dọc từ dưới cung top-right xuống đáy
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(size.width, radius),
                        end = Offset(size.width, defaultSize),
                        strokeWidth = stroke
                    )

                    //duong chinh giua canh ben phai
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(size.width, (size.height - defaultSize) / 2),
                        end = Offset(size.width, (size.height + defaultSize) / 2),
                        strokeWidth = stroke
                    )

                    //Bottom-right
                    drawCustomArc(
                        color = borderColor,
                        startAngle = 0f, // bắt đầu từ phải (3h), quét xuống dưới (6h)
                        topLeft = Offset(
                            size.width - radius * 2,
                            size.height - radius * 2
                        ), // góc trái-trên của hình cung
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                    // viền ngang dưới (từ trái đến trước cung bottom-right)
                    drawCustomLine(
                        color = borderColor,
                        start = Offset((size.width - defaultSize), size.height),
                        end = Offset(size.width - radius, size.height),
                        strokeWidth = stroke
                    )
                    //viền dọc phải (từ trên đến trước cung)
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(size.width, (size.height - defaultSize)),
                        end = Offset(size.width, size.height - radius),
                        strokeWidth = stroke
                    )

                    //duong ngang chinh giua canh bottom
                    drawCustomLine(
                        color = borderColor,
                        start = Offset((size.width - defaultSize) / 2, size.height),
                        end = Offset((size.width + defaultSize) / 2, size.height),
                        strokeWidth = stroke
                    )

                    //Bottom-left
                    drawCustomArc(
                        color = borderColor,
                        startAngle = 90f, // bắt đầu từ dưới (6h), quét sang trái (9h)
                        topLeft = Offset(0f, size.height - radius * 2),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                    //viền ngang dưới (bên phải cung bottom-left)
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(radius, size.height),
                        end = Offset(defaultSize, size.height),
                        strokeWidth = stroke
                    )
                    //viền dọc trái (từ trên đến trước cung)
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(0f, (size.height - defaultSize)),
                        end = Offset(0f, size.height - radius),
                        strokeWidth = stroke
                    )

                    //duong chinh giua canh ben trai
                    drawCustomLine(
                        color = borderColor,
                        start = Offset(0f, (size.height - defaultSize) / 2),
                        end = Offset(0f, (size.height + defaultSize) / 2),
                        strokeWidth = stroke
                    )
                }


                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            onCaptureRegionChange(captureRegion)
                        },
                        onDrag = { change, dragAmountPx ->
                            change.consume()
                            val dragAmount =
                                Offset(dragAmountPx.x / density, dragAmountPx.y / density)
                            val defaultSize = (dfSize / 2).toPx()

                            when (resizeType) {
                                ResizeType.TOP_LEFT -> {
                                    val newLeft = (captureRegion.left + dragAmount.x).coerceIn(
                                        0f,
                                        captureRegion.right - defaultSize
                                    )
                                    val newTop = (captureRegion.top + dragAmount.y).coerceIn(
                                        0f,
                                        captureRegion.bottom - defaultSize
                                    )
                                    captureRegion = captureRegion.copy(left = newLeft, top = newTop)
                                }

                                ResizeType.TOP_RIGHT -> {
                                    val newRight = (captureRegion.right + dragAmount.x).coerceIn(
                                        captureRegion.left + defaultSize,
                                        screenWidth.toPx() / density
                                    )
                                    val newTop = (captureRegion.top + dragAmount.y).coerceIn(
                                        0f,
                                        captureRegion.bottom - defaultSize
                                    )
                                    captureRegion =
                                        captureRegion.copy(right = newRight, top = newTop)
                                }

                                ResizeType.BOTTOM_LEFT -> {
                                    val newLeft = (captureRegion.left + dragAmount.x).coerceIn(
                                        0f,
                                        captureRegion.right - defaultSize
                                    )
                                    val newBottom = (captureRegion.bottom + dragAmount.y).coerceIn(
                                        captureRegion.top + defaultSize,
                                        screenHeight.toPx() / density
                                    )
                                    captureRegion =
                                        captureRegion.copy(left = newLeft, bottom = newBottom)
                                }

                                ResizeType.BOTTOM_RIGHT -> {
                                    val newRight = (captureRegion.right + dragAmount.x).coerceIn(
                                        captureRegion.left + defaultSize,
                                        screenWidth.toPx() / density
                                    )
                                    val newBottom = (captureRegion.bottom + dragAmount.y).coerceIn(
                                        captureRegion.top + defaultSize,
                                        screenHeight.toPx() / density
                                    )
                                    captureRegion =
                                        captureRegion.copy(right = newRight, bottom = newBottom)
                                }

                                ResizeType.TOP -> {
                                    val newTop = (captureRegion.top + dragAmount.y).coerceIn(
                                        0f,
                                        captureRegion.bottom - defaultSize
                                    )
                                    captureRegion = captureRegion.copy(top = newTop)
                                }

                                ResizeType.BOTTOM -> {
                                    val newBottom = (captureRegion.bottom + dragAmount.y).coerceIn(
                                        captureRegion.top + defaultSize,
                                        screenHeight.toPx() / density
                                    )
                                    captureRegion = captureRegion.copy(bottom = newBottom)
                                }

                                ResizeType.LEFT -> {
                                    val newLeft = (captureRegion.left + dragAmount.x).coerceIn(
                                        0f,
                                        captureRegion.right - defaultSize
                                    )
                                    captureRegion = captureRegion.copy(left = newLeft)
                                }

                                ResizeType.RIGHT -> {
                                    val newRight = (captureRegion.right + dragAmount.x).coerceIn(
                                        captureRegion.left + defaultSize,
                                        screenWidth.toPx() / density
                                    )
                                    captureRegion = captureRegion.copy(right = newRight)
                                }

                                else -> {
                                    val newLeft = (captureRegion.left + dragAmount.x).coerceIn(
                                        0f,
                                        captureRegion.right - defaultSize
                                    )
                                    val newTop = (captureRegion.top + dragAmount.y).coerceIn(
                                        0f,
                                        captureRegion.bottom - defaultSize
                                    )
                                    val newRight = (captureRegion.right + dragAmount.x).coerceIn(
                                        captureRegion.left + defaultSize,
                                        screenWidth.toPx() / density
                                    )
                                    val newBottom = (captureRegion.bottom + dragAmount.y).coerceIn(
                                        captureRegion.top + defaultSize,
                                        screenHeight.toPx() / density
                                    )
                                    captureRegion = captureRegion.copy(
                                        left = newLeft,
                                        top = newTop,
                                        right = newRight,
                                        bottom = newBottom
                                    )
                                }
                            }
                        }
                    )
                }

                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            // Người dùng vừa nhấn xuống
                            onChangeTextVisibility(false)

                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            val defaultSize = dfSize.toPx()
                            // Kiểm tra xem người dùng đang tương tác với góc hoặc cạnh nào
                            resizeType = getResizeType(offset, w, h, defaultSize)
                            Log.d("ResizeType", "width: $w, height: $h")
                            Log.d(
                                "ResizeType",
                                "resizeType: $resizeType, OffsetInDp: $offset, CaptureRegion: $captureRegion"
                            )

                            try {
                                awaitRelease()
                                onChangeTextVisibility(true)
                            }  catch (e: Exception) {
                                e.printStackTrace()
                            }
                            finally {
                                onChangeTextVisibility(true)
                            }
                        },
                    )
                }
        )
    }
}


// Preview
@Preview(showBackground = true)
@Composable
fun ScreenCaptureOverlayPreview() {
    var captureRegion by remember { mutableStateOf(Rect(0f, 0f, 200f, 200f)) }
    CropAreaAuto(
        onCaptureRegionChange = {},
        onClear = {},
        onCheck = {},
        onChangeTextVisibility = {},
        captureRegionParam = captureRegion
    )
}