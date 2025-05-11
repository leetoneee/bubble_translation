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
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistry
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeViewModel
import com.bteamcoding.bubbletranslation.core.utils.MediaProjectionSingleton
import com.bteamcoding.bubbletranslation.core.utils.VirtualDisplaySignleton
//import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.CaptureRegion
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.CaptureRegion
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FullscreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FullscreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CoatingLayer
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CropArea
import kotlin.math.abs

class PartialScreenModeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var mediaProjectionManager: MediaProjectionManager

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var viewModel: PartialScreenModeViewModel

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    // Thêm biến theo dõi vị trí
    private var initialX = 0
    private var initialY = 0
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

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

                // Kiểm tra captureRegion và gọi startScreenshot nếu có
                state.captureRegion?.let { captureRegion ->
                    // Logic Crop Area: sử dụng captureRegion để cắt màn hình
                    CropArea(
                        captureRegion = captureRegion,
                        onCaptureRegionChange = { newRegion ->
                            // Cập nhật captureRegion qua ViewModel
                            viewModel.onAction(PartialScreenModeAction.SetCaptureRegion(newRegion))
                        }
                    )
                    startScreenshot(captureRegion)
                }

//                state.visionText?.let {
//                    Log.d("PartialScreenModeService", "Creating ComposeView ${it.text}")
//                    CoatingLayer(
//                        text = it,
//                        onDrag = { offsetY ->
//                            // Cập nhật vị trí và layout
//                            params.y += offsetY.toInt()
//
//                            params.y = if (params.y > 0) 0 else params.y
//                            windowManager.updateViewLayout(floatingView, layoutParams)
//                        },
//                        onDragEnd = {
//                            if (abs(params.y - 0) > 50) {
//                                viewModel.onAction(PartialScreenModeAction.OnReset)
//                                stopSelf()
//                            } else {
//                                params.y = 0
//                                windowManager.updateViewLayout(floatingView, layoutParams)
//                            }
//                        }
//                    )
//                }
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
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = initialX
            y = initialY
            gravity = Gravity.TOP or Gravity.START
        }

        // Log để kiểm tra WindowManager
        Log.d("PartialScreenModeService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParams)
            Log.d("FullScreenModeService", "View added successfully")
        } catch (e: Exception) {
            Log.e("FullScreenModeService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("PartialScreenModeService", "Service started")

        if (MediaProjectionSingleton.mediaProjection == null) {
            val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: return START_NOT_STICKY
            val resultData = intent.getParcelableExtra<Intent>("resultData") ?: return START_NOT_STICKY

            mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            MediaProjectionSingleton.mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)
        } else {
            Log.d("PartialScreenModeService", "Using existing MediaProjection instance.")
        }

        // Kiểm tra captureRegion từ ViewModel và gọi startScreenshot khi có giá trị
        try {
            val captureRegion = if (::viewModel.isInitialized) {
                // Nếu viewModel đã được khởi tạo, lấy captureRegion từ viewModel
                viewModel.state.value.captureRegion
            } else {
                // Nếu viewModel là null, sử dụng giá trị mặc định
                CaptureRegion(50, 50, 100, 100)
            }
            captureRegion?.let {
                startScreenshot(it)
            }
            // Tiến hành xử lý khi captureRegion có giá trị hợp lệ
        } catch (e: Exception) {
            // Xử lý lỗi khi không thể truy cập captureRegion
            Log.e("PartialScreenModeService", "Error accessing captureRegion: ${e.message}")
        }

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

    private fun startScreenshot(captureRegion: CaptureRegion) {
//      Lấy thông tin về màn hình
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

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
        MediaProjectionSingleton.mediaProjection?.registerCallback(mediaProjectionCallback, Handler(Looper.getMainLooper()))

        // Nếu VirtualDisplay đã tồn tại, chỉ cần cập nhật Surface mới
        if (VirtualDisplaySignleton.virtualDisplay != null) {
            // Cập nhật Surface cho VirtualDisplay thay vì tạo mới
            VirtualDisplaySignleton.virtualDisplay?.surface = imageReader.surface
        } else {
            // Nếu VirtualDisplay chưa tồn tại, tạo mới
            try {
                VirtualDisplaySignleton.virtualDisplay = MediaProjectionSingleton.mediaProjection?.createVirtualDisplay(
                    "ScreenCapture",
                    width, height, density,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.surface, null, null
                )
                if (VirtualDisplaySignleton.virtualDisplay == null) {
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
                bitmap, captureRegion.startX, captureRegion.startY,
                captureRegion.endX - captureRegion.startX, captureRegion.endY - captureRegion.startY
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
            VirtualDisplaySignleton.virtualDisplay?.release()
            VirtualDisplaySignleton.virtualDisplay = null
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
                viewModel.onAction(PartialScreenModeAction.OnChange(result))
            } catch (e: Exception) {
                Log.e("PartialScreenOCR", "Error: ${e.message}")
            }
        }
    }

}