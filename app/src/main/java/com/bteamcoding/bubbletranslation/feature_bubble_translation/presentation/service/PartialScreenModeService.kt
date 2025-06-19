package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service

//import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.CaptureRegion
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
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
import com.bteamcoding.bubbletranslation.app.data.local.MediaProjectionPermissionHolder
import com.bteamcoding.bubbletranslation.core.utils.MediaProjectionSingleton
import com.bteamcoding.bubbletranslation.core.utils.VirtualDisplaySingleton
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.core.utils.translateVisionText
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CropArea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.TextOverlayCrop

class PartialScreenModeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val resultCode = MediaProjectionPermissionHolder.resultCode
    private val resultData = MediaProjectionPermissionHolder.resultData

    private lateinit var mediaProjectionManager: MediaProjectionManager

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var viewModel: PartialScreenModeViewModel

    private var state by mutableStateOf(PartialScreenModeState())

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var statusBarHeight: Int = 0

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
        Log.d("PartialScreenModeService", "Service is now running in the foreground")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@PartialScreenModeService)
            setViewTreeViewModelStoreOwner(this@PartialScreenModeService)
            setViewTreeSavedStateRegistryOwner(this@PartialScreenModeService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("PartialScreenModeService", "Creating ComposeView")

            setContent {
                viewModel =
                    ViewModelProvider(this@PartialScreenModeService)[PartialScreenModeViewModel::class.java]
                val state by viewModel.state.collectAsState()
                val params = layoutParams as WindowManager.LayoutParams

                // Logic Crop Area: sử dụng captureRegion để cắt màn hình
                var isResizingOrDragging by remember { mutableStateOf(false) }

                CropArea(
                    state = state,
                    onCaptureRegionChange = { newRegion ->
                        Log.i("captureRegion", newRegion.toString())
                        // Cập nhật captureRegion qua ViewModel khi người dùng thay đổi nó
                        viewModel.onAction(PartialScreenModeAction.SetCaptureRegion(newRegion))
                        viewModel.onAction(PartialScreenModeAction.OnChange(null))
                        viewModel.onAction(PartialScreenModeAction.OnChangeTextVisibility(false))
                        startScreenshot(newRegion)
                    },
                    onTap = {
                        viewModel.onAction(PartialScreenModeAction.OnReset)
                        stopSelf()
                    },
                    onResizeStateChanged = {
                        isResizingOrDragging = it
                        viewModel.onAction(PartialScreenModeAction.OnChangeTextVisibility(false))
                    },
                    onChangeTextVisibility = {
                        viewModel.onAction(PartialScreenModeAction.OnChangeTextVisibility(it))
                    }
                )
                //startScreenshot(state.captureRegion)
                if (!isResizingOrDragging && state.isTextVisibility) {
                    state.translatedVisionText?.let {
                        Log.d("PartialScreenModeService", "Creating ComposeView ${it.text}")
                        TextOverlayCrop(
                            visionText = state.translatedVisionText!!,
                            captureRegion = state.captureRegion
                        )
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
        Log.d("PartialScreenModeService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParams)
            Log.d("PartialScreenModeService", "View added successfully")
        } catch (e: Exception) {
            Log.e("PartialScreenModeService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("PartialScreenModeService", "Service started")
        viewModel =
            ViewModelProvider(this@PartialScreenModeService)[PartialScreenModeViewModel::class.java]

        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        statusBarHeight = sharedPreferences.getInt("STATUS_BAR_HEIGHT", 0)
        Log.d("PartialScreenModeService", "StatusBar Height received: $statusBarHeight")

        if (MediaProjectionSingleton.mediaProjection == null) {
//            val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED)
//                ?: return START_NOT_STICKY
//            val resultData =
//                intent.getParcelableExtra<Intent>("resultData") ?: return START_NOT_STICKY

            mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            MediaProjectionSingleton.mediaProjection =
                resultData?.let { mediaProjectionManager.getMediaProjection(resultCode, it) }
        } else {
            Log.d("PartialScreenModeService", "Using existing MediaProjection instance.")
        }

        startScreenshot(state.captureRegion)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PartialScreenModeService", "Service onDestroy called")

        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
                Log.d("PartialScreenModeService", "View removed successfully")
            } catch (e: Exception) {
                Log.e("PartialScreenModeService", "Error removing view: ${e.message}")
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

    private fun startScreenshot(captureRegion: Rect) {
        Log.d("PartialScreenModeService", "StatusBar Height received: $statusBarHeight")

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
            Log.e("PartialScreenModeService", "MediaProjection is not initialized")
            stopSelf() // Dừng service nếu mediaProjection không hợp lệ
            return
        }

        // Register the mediaProjection callback after initializing mediaProjection
        val mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("PartialScreenModeService", "MediaProjection stopped")
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
                    Log.e("PartialscreenModeService", "Failed to create virtual display")
                    releaseResources()
                    stopSelf() // Dừng service nếu không thể tạo virtual display
                    return
                }
            } catch (e: Exception) {
                Log.e("PartialscreenModeService", "Error creating virtual display: ${e.message}")
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
                (captureRegion.left * density).toInt(),
                (captureRegion.top * density + statusBarHeight).toInt(),
                (captureRegion.right * density - captureRegion.left * density).toInt(),
                (captureRegion.bottom * density - captureRegion.top * density).toInt()
            )

//          Dọn dẹp
            image.close()
            reader.close()
            //releaseResources()

            processBitmap(croppedBitmap)
//            stopSelf()
        }, Handler(Looper.getMainLooper()))
    }

    private fun releaseResources() {
        // Giải phóng tài nguyên MediaProjection và VirtualDisplay
        try {
            VirtualDisplaySingleton.virtualDisplay?.release()
            VirtualDisplaySingleton.virtualDisplay = null
            MediaProjectionSingleton.mediaProjection?.stop()
            Log.d("PartialscreenModeService", "Resources released successfully")
        } catch (e: Exception) {
            Log.e("PartialscreenModeService", "Error releasing resources: ${e.message}")
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = recognizeTextFromImage(bitmap)
                Log.d("PartialScreenOCR", "Detected text: ${result.text}")
                val translatedResult = translateVisionText(result)

                viewModel.onAction(PartialScreenModeAction.OnChange(result))
                viewModel.onAction(PartialScreenModeAction.OnChangeTranslatedVisionText(translatedResult))
                viewModel.onAction(PartialScreenModeAction.OnChangeTextVisibility(true))
            } catch (e: Exception) {
                Log.e("PartialScreenOCR", "Error: ${e.message}")
            }
        }
    }


}