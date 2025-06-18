package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.utils.MediaProjectionSingleton
import com.bteamcoding.bubbletranslation.core.utils.VirtualDisplaySingleton
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.BottomSheetOnScreen
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CropArea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.TextOverlayCrop
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryViewModel
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryViewModel2
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlin.math.sqrt

@AndroidEntryPoint
class WordScreenModeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var mediaProjectionManager: MediaProjectionManager

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var viewModel: WordScreenModeViewModel
//    private lateinit var dicViewModel: DictionaryViewModel


    private var state by mutableStateOf(WordScreenModeState())

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private var statusBarHeight: Int = 0

    private var paramsX: Int = 0
    private var paramsY: Int = 0

    private var widthBee: Int = 304
    private var heightBee: Int = 148


    //private var captureRegion: CaptureRegion? = null

    override fun onCreate() {
        super.onCreate()

        // Đảm bảo rằng chúng ta đang khởi động service dưới dạng foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "media_projection_channel",
                "Media Projection Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification =
            NotificationCompat.Builder(this, "media_projection_channel")
                .setContentTitle("Bubble Translation")
                .setContentText("Capturing screen...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Sử dụng biểu tượng của bạn
                .build()

        // Bắt đầu foreground service
        val notificationId = 1
        startForeground(notificationId, notification)

        // Tiến hành thực hiện các tác vụ khác (như yêu cầu quyền ghi màn hình...)
        Log.d("WordScreenModeService", "Service is now running in the foreground")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@WordScreenModeService)
            setViewTreeViewModelStoreOwner(this@WordScreenModeService)
            setViewTreeSavedStateRegistryOwner(this@WordScreenModeService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("WordScreenModeService", "Creating ComposeView")

            setContent {
                viewModel =
                    ViewModelProvider(this@WordScreenModeService)[WordScreenModeViewModel::class.java]
                val state by viewModel.state.collectAsState()
                if (state.isShowBottomSheet) {
                    if (state.sourceText != null && state.sourceText != "") {
                        BottomSheetOnScreen(
                            onDismissRequest = {
                                viewModel.onAction(
                                    WordScreenModeAction.OnShowBottomSheet(
                                        false
                                    )
                                )
                                viewModel.onAction(WordScreenModeAction.OnReset)
                                stopSelf()
                            },
                            onTap = {
                                viewModel.onAction(
                                    WordScreenModeAction.OnShowBottomSheet(
                                        false
                                    )
                                )
                                viewModel.onAction(WordScreenModeAction.OnReset)
                                stopSelf()
                            },
                            sourceText = state.sourceText!!,
//                            viewModel = dicViewModel,
                        )
                    } else {
                        viewModel.onAction(
                            WordScreenModeAction.OnShowBottomSheet(
                                false
                            )
                        )
                        viewModel.onAction(WordScreenModeAction.OnReset)
                        stopSelf()
                    }
                }
            }
        }

        // Set LayoutParams for Floating Widget
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = 0
            y = 0
            gravity = Gravity.TOP or Gravity.START
        }

        // Log để kiểm tra WindowManager
        Log.d("WordScreenModeService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParams)
            Log.d("WordScreenModeService", "View added successfully")
        } catch (e: Exception) {
            Log.e("WordScreenModeService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("WordScreenModeService", "Service started")

        paramsX = intent?.getIntExtra("params_x", 0) ?: 0
        paramsY = intent?.getIntExtra("params_y", 0) ?: 0

        viewModel =
            ViewModelProvider(this@WordScreenModeService)[WordScreenModeViewModel::class.java]

        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        statusBarHeight = sharedPreferences.getInt("STATUS_BAR_HEIGHT", 0)
        Log.d("WordScreenModeService", "StatusBar Height received: $statusBarHeight")

        if (MediaProjectionSingleton.mediaProjection == null) {
            val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED)
                ?: return START_NOT_STICKY
            val resultData =
                intent.getParcelableExtra<Intent>("resultData") ?: return START_NOT_STICKY

            mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            MediaProjectionSingleton.mediaProjection =
                mediaProjectionManager.getMediaProjection(resultCode, resultData)
        } else {
            Log.d("WordScreenModeService", "Using existing MediaProjection instance.")
        }

        startScreenshot()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("WordScreenModeService", "Service onDestroy called")

        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
                Log.d("WordScreenModeService", "View removed successfully")
            } catch (e: Exception) {
                Log.e("WordScreenModeService", "Error removing view: ${e.message}")
            }
        }

        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }

    // Implement LifecycleOwner
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    // Implement ViewModelStoreOwner
    override val viewModelStore: ViewModelStore
        get() = viewModelStoreInstance

    // Implement SavedStateRegistryOwner
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private fun startScreenshot() {
        Log.d("WordScreenModeService", "StatusBar Height received: $statusBarHeight")

//      Lấy thông tin về màn hình
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val d = metrics.densityDpi
        val density = metrics.density


//        Tạo ImageReader
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        // Kiểm tra nếu mediaProjection còn hợp lệ
        if (MediaProjectionSingleton.mediaProjection == null) {
            Log.e("WordScreenModeService", "MediaProjection is not initialized")
            stopSelf() // Dừng service nếu mediaProjection không hợp lệ
            return
        }

        // Register the mediaProjection callback after initializing mediaProjection
        val mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("WordScreenModeService", "MediaProjection stopped")
                // Giải phóng tài nguyên khi MediaProjection dừng
                releaseResources()
            }
        }
        MediaProjectionSingleton.mediaProjection?.registerCallback(
            mediaProjectionCallback,
            Handler(Looper.getMainLooper())
        )

        // Nếu VirtualDisplay đã tồn tại, chỉ cần cập nhật Surface mới
        if (VirtualDisplaySingleton.virtualDisplay != null) {
            // Cập nhật Surface cho VirtualDisplay thay vì tạo mới
            VirtualDisplaySingleton.virtualDisplay?.surface = imageReader.surface
        } else {
            // Nếu VirtualDisplay chưa tồn tại, tạo mới
            try {
                VirtualDisplaySingleton.virtualDisplay =
                    MediaProjectionSingleton.mediaProjection?.createVirtualDisplay(
                        "ScreenCapture",
                        width, height, d,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.surface, null, null
                    )
                if (VirtualDisplaySingleton.virtualDisplay == null) {
                    Log.e("WordscreenModeService", "Failed to create virtual display")
                    releaseResources()
                    stopSelf() // Dừng service nếu không thể tạo virtual display
                    return
                }
            } catch (e: Exception) {
                Log.e("WordscreenModeService", "Error creating virtual display: ${e.message}")
                e.printStackTrace()
                releaseResources()
                stopSelf() // Dừng service nếu có lỗi
                return
            }
        }


        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

//          Tạo ảnh Bitmap
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)


            // Crop the image based on CaptureRegion
            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                paramsX,
                paramsY + statusBarHeight,
                widthBee,
                heightBee
            )

//          Dọn dẹp
            image.close()
            reader.close()
            //releaseResources()

            processBitmap(croppedBitmap)
            Log.d("WordscreenModeService", "closestText 1: ${state.sourceText}")
//            stopSelf()
        }, Handler(Looper.getMainLooper()))
    }

    private fun releaseResources() {
        // Giải phóng tài nguyên MediaProjection và VirtualDisplay
        try {
            VirtualDisplaySingleton.virtualDisplay?.release()
            VirtualDisplaySingleton.virtualDisplay = null
            MediaProjectionSingleton.mediaProjection?.stop()
            Log.d("WordscreenModeService", "Resources released successfully")
        } catch (e: Exception) {
            Log.e("WordscreenModeService", "Error releasing resources: ${e.message}")
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = recognizeTextFromImage(bitmap)
                Log.d("WordScreenOCR", "Detected text: ${result.text}")

                val closestText = getClosestText(result)
                viewModel.onAction(WordScreenModeAction.OnChangeSourceText(closestText))
                viewModel.onAction(WordScreenModeAction.OnShowBottomSheet(true))

            } catch (e: Exception) {
                Log.e("WordScreenOCR", "Error: ${e.message}")
            }
        }
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        return sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toFloat())
    }

    fun getClosestText(recognizedTexts: Text): String {
        var closestText = ""
        var minDistance = Float.MAX_VALUE

        // Tính tọa độ trung tâm của param (floating view)
        val centerXParam = paramsX
        val centerYParam = paramsY

        // Duyệt qua các văn bản nhận dạng và tính toán khoảng cách
        // Duyệt qua từng textBlock
        recognizedTexts.textBlocks.forEach { block ->
            // Duyệt qua từng line trong block
            block.lines.forEach { line ->
                // Duyệt qua từng element trong line
                line.elements.forEach { element ->
                    // Tính tọa độ trung tâm của element (x, y) từ frame của element
                    val centerX = (element.boundingBox?.left ?: 0)
                    val centerY = (element.boundingBox?.top ?: 0)

                    // Tính khoảng cách giữa tọa độ (params.x, params.y) và tọa độ trung tâm của element
                    val distance = distance(widthBee / 3, heightBee / 3, centerX, centerY)

                    // Nếu khoảng cách nhỏ hơn khoảng cách tối thiểu hiện tại, cập nhật văn bản gần nhất
                    if (distance < minDistance) {
                        minDistance = distance
                        closestText = element.text
                    }
                }
            }
        }

        return closestText
    }
}