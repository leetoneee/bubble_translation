package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeState

@SuppressLint("LocalContextConfigurationRead", "ConfigurationScreenWidthHeight")
@Composable
fun CropArea(
    state: PartialScreenModeState,
    onCaptureRegionChange: (Rect) -> Unit,
    onTap: () -> Unit,
    onResizeStateChanged: (Boolean) -> Unit,
    onChangeTextVisibility: (Boolean) -> Unit,
) {
    var captureRegion by remember { mutableStateOf(Rect(0f, 0f, 300f, 400f)) }
    var resizeType by remember { mutableStateOf<ResizeType?>(null) }

    // Lấy chiều rộng và chiều cao màn hình từ Configuration
    val context = LocalContext.current
    val screenWidth = context.resources.configuration.screenWidthDp.dp // Chiều rộng màn hình
    val screenHeight = context.resources.configuration.screenHeightDp.dp  // Chiều cao màn hình
    Log.d("ScreenSize", "width: $screenWidth, height: $screenHeight")
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isDragging by remember { mutableStateOf(false) }  // Trạng thái kéo

    val dfSize = 30.dp

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
            .pointerInput(Unit) {

                detectDragGestures(
                    onDragStart = {
                        isDragging = true  // Đánh dấu là đang kéo
//                        onDragStart()
                    },
                    onDrag = { _, dragAmount ->
                        offsetY += dragAmount.y
//                        onDrag(dragAmount.y)  // Cập nhật vị trí khi kéo
                    },
                    onDragEnd = {
                        isDragging = false  // Khi kết thúc kéo, đặt lại trạng thái kéo
//                        onDragEnd()
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Nếu không có kéo, gọi stopSelf()
                        if (!isDragging) {
                            onTap()
                        }
                    }
                )
            }
    ) {

        // Box 2: Hình chữ nhật có thể thay đổi kích thước và kéo
        val greenMedium = colorResource(R.color.green_medium)
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
                    val borderColor = greenMedium
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
                        onDragStart = {
                            isDragging = true
                            onResizeStateChanged(true)
                        },
                        onDragEnd = {
                            isDragging = false
                            onResizeStateChanged(false)
                            onCaptureRegionChange(captureRegion)
                        },
                        onDragCancel = {
                            isDragging = false
                            onResizeStateChanged(false)
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


enum class ResizeType {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT,
    NONE
}

fun getResizeType(offset: Offset, w: Float, h: Float, defaultSize: Float): ResizeType {
    // Kiểm tra xem người dùng có chạm vào góc hay cạnh nào để thay đổi kích thước

    return when {
        offset.x in (0f)..(defaultSize) && offset.y in (0f)..(defaultSize) -> ResizeType.TOP_LEFT
        offset.x in (w - defaultSize)..(w) && offset.y in (0f)..(defaultSize) -> ResizeType.TOP_RIGHT
        offset.x in (0f)..(defaultSize) && offset.y in (h - defaultSize)..(h) -> ResizeType.BOTTOM_LEFT
        offset.x in (w - defaultSize)..(w) && offset.y in (h - defaultSize)..(h) -> ResizeType.BOTTOM_RIGHT
        offset.x in (0f)..(defaultSize) && offset.y in ((h - defaultSize) / 2)..((h + defaultSize) / 2) -> ResizeType.LEFT
        offset.x in (w - defaultSize)..(w) && offset.y in ((h - defaultSize) / 2)..((h + defaultSize) / 2) -> ResizeType.RIGHT
        offset.x in ((w - defaultSize) / 2)..((w + defaultSize) / 2) && offset.y in (0f)..(defaultSize) -> ResizeType.TOP
        offset.x in ((w - defaultSize) / 2)..((w + defaultSize) / 2) && offset.y in (h - defaultSize)..(h) -> ResizeType.BOTTOM
        else -> ResizeType.NONE
    }
}

fun DrawScope.drawCustomArc(
    color: Color,
    startAngle: Float,
    topLeft: Offset,
    size: Size,
    style: DrawStyle = Stroke(width = 1f)
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = 90f,         // thường dùng cho bo góc
        useCenter = false,
        topLeft = topLeft,
        size = size,
        style = style
    )
}

fun DrawScope.drawCustomLine(
    color: Color,
    start: Offset,
    end: Offset,
    strokeWidth: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth
    )
}

fun DrawScope.drawCustomRect(
    color: Color,
    topLeft: Offset,
    size: Size,
) {
    drawRect(
        color = color,
        topLeft = topLeft,
        size = size,
    )
}


//// Preview
//@Preview(showBackground = true)
//@Composable
//fun ScreenCaptureOverlayPreview() {
//    var captureRegion by remember { mutableStateOf(Rect(0f, 0f, 200f, 200f)) }
//    CropArea(
//        captureRegion = captureRegion,
//        onCaptureRegionChange = { newRegion -> captureRegion = newRegion }
//    )
//}