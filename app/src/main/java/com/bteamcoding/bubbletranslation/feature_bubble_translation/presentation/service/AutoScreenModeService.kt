package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service

import android.animation.ValueAnimator
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
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
import com.bteamcoding.bubbletranslation.core.utils.MediaProjectionSingleton
import com.bteamcoding.bubbletranslation.core.utils.VirtualDisplaySignleton
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AutoScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AutoScreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AutoScreenModeState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.AutoArea
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CropArea
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CropAreaAuto
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.TextOverlayAuto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.TextOverlayCrop
import kotlinx.coroutines.delay


class AutoScreenModeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var mediaProjectionManager: MediaProjectionManager

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var textOverlayView: ComposeView      // Chứa TextOverlayAuto

    private lateinit var layoutParamsAutoArea: WindowManager.LayoutParams
    private lateinit var layoutParamsCropArea: WindowManager.LayoutParams
    private lateinit var layoutParamsTextOverlay: WindowManager.LayoutParams

    private lateinit var viewModel: AutoScreenModeViewModel

    private var state by mutableStateOf(AutoScreenModeState())

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var statusBarHeight: Int = 0

    private var initialX = 0
    private var initialY = 0

    var isTranslating by mutableStateOf(false)
    var showAutoArea by mutableStateOf(false)

    var isPlaying by mutableStateOf(false)

    var showOverlay by mutableStateOf(false)

    private var previousBitmap by mutableStateOf<Bitmap?>(null)

    private var handler: Handler? = null
    private var screenshotRunnable: Runnable? = null

    //private var captureRegion: CaptureRegion? = null

    private val minX = 25
    private val minY = 25
    private val midY = screenHeight / 2

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
        Log.d("AutoScreenModeService", "Service is now running in the foreground")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@AutoScreenModeService)
            setViewTreeViewModelStoreOwner(this@AutoScreenModeService)
            setViewTreeSavedStateRegistryOwner(this@AutoScreenModeService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("AutoScreenModeService", "Creating ComposeView")

            setContent {
                viewModel =
                    ViewModelProvider(this@AutoScreenModeService)[AutoScreenModeViewModel::class.java]
                val state by viewModel.state.collectAsState()

                //      Lấy thông tin về màn hình
                val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                val metrics = DisplayMetrics()
                display.getRealMetrics(metrics)

                val density = metrics.density
                val widthPx = ((state.captureRegion.right - state.captureRegion.left) * density).toInt()

                layoutParamsAutoArea = WindowManager.LayoutParams(
                    widthPx,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        WindowManager.LayoutParams.TYPE_PHONE
                    },
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    x = ((state.captureRegion.left) * density).toInt()
                    y = ((state.captureRegion.top - 40) * density).toInt()
                    gravity = Gravity.TOP or Gravity.START
                }

                layoutParamsCropArea = WindowManager.LayoutParams(
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

                // Khi showAutoArea thay đổi, cập nhật layoutParams của floatingView
                LaunchedEffect(showAutoArea) {
                    val params = if (showAutoArea) layoutParamsAutoArea else layoutParamsCropArea
                    try {
                        windowManager.updateViewLayout(floatingView, params)
                    } catch (e: Exception) {
                        Log.e("AutoScreenModeService", "Error updating layout params: ${e.message}")
                    }
                }

                if (showAutoArea) {
                    AutoArea(
                        captureRegion = state.captureRegion,
                        onClose = {
                            showOverlay = false
                            textOverlayView.visibility = View.GONE

                            viewModel.onAction(AutoScreenModeAction.OnReset)
                            stopSelf()
                        },
                        onEdit = {
                            showAutoArea = false
                            isPlaying = false
                            showOverlay = false
                        },
                        isPlaying = isPlaying,
                        onChangePlaying = {
                            isPlaying = !isPlaying
                        }
                    )
                } else {
                    CropAreaAuto(
                        captureRegionParam = state.captureRegion,
                        onCaptureRegionChange = { newRegion ->
                            Log.i("captureRegion", newRegion.toString())
                            // Cập nhật captureRegion qua ViewModel khi người dùng thay đổi nó
                            viewModel.onAction(AutoScreenModeAction.SetCaptureRegion(newRegion))
                            viewModel.onAction(AutoScreenModeAction.OnChange(null))
                            viewModel.onAction(AutoScreenModeAction.OnChangeTextVisibility(false))
                        },
                        onClear = {
                            viewModel.onAction(AutoScreenModeAction.OnReset)
                            stopSelf()
                        },
                        onCheck = {
                            showAutoArea = true
                            isPlaying = true
                        },
                        onChangeTextVisibility = {
                            viewModel.onAction(AutoScreenModeAction.OnChangeTextVisibility(it))
                        }
                    )
                }

                LaunchedEffect(isPlaying) {
                    if (isPlaying) {
                        showOverlay = true
                        viewModel.onAction(AutoScreenModeAction.SetCaptureRegion(state.captureRegion))
                        Log.d("AutoScreenModeService", "CaptureRegion: ${state.captureRegion}")
                        startContinuousScreenshot(state.captureRegion)

                    } else {
                        stopContinuousScreenshot()
                    }
                }

            }
        }

        // Tạo textOverlayView riêng cho TextOverlayAuto
        textOverlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@AutoScreenModeService)
            setViewTreeViewModelStoreOwner(this@AutoScreenModeService)
            setViewTreeSavedStateRegistryOwner(this@AutoScreenModeService)

            setContent {
                viewModel =
                    ViewModelProvider(this@AutoScreenModeService)[AutoScreenModeViewModel::class.java]
                val state by viewModel.state.collectAsState()

                // Lắng nghe sự thay đổi của showOverlay và cập nhật lại UI
                LaunchedEffect(showOverlay) {
                    // Khi showOverlay thay đổi, sẽ cập nhật lại widget
                    if (!showOverlay) {
                        // Nếu showOverlay = false, ẩn TextOverlayAuto
                        textOverlayView.visibility = View.GONE
                    } else {
                        // Nếu showOverlay = true, hiển thị TextOverlayAuto
                        textOverlayView.visibility = View.VISIBLE
                    }
                }

                //      Lấy thông tin về màn hình
                val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                val metrics = DisplayMetrics()
                display.getRealMetrics(metrics)

                val density = metrics.density
                val widthPx = ((state.captureRegion.right - state.captureRegion.left) * density).toInt()
                val screenHeightDp = metrics.heightPixels / density

                // Tính toán y theo điều kiện
                val y: Int = if (state.captureRegion.top > screenHeightDp / 2) {
                    // Nếu captureRegion.top < 1/2 chiều cao màn hình
                    ((state.captureRegion.top - 40 - (state.captureRegion.bottom - state.captureRegion.top) / 2) * density).toInt()
                } else {
                    // Nếu captureRegion.top >= 1/2 chiều cao màn hình
                    ((state.captureRegion.bottom * density).toInt())
                }

                LaunchedEffect(state.captureRegion) {
                // Set LayoutParams for Floating Widget
                    layoutParamsTextOverlay = WindowManager.LayoutParams(
                        widthPx,
                        WindowManager.LayoutParams.WRAP_CONTENT,
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
                        x = ((state.captureRegion.left) * density).toInt()
                        this.y = y
                        gravity = Gravity.TOP or Gravity.START
                    }
                    // Sau khi cập nhật layoutParamsTextOverlay, thêm view vào WindowManager
                    windowManager.updateViewLayout(textOverlayView, layoutParamsTextOverlay)
                }


                if (state.visionText != null) {
                    TextOverlayAuto(
                        visionText = state.visionText!!,
                        captureRegion = state.captureRegion,
                        onDrag = { offsetX, offsetY ->
                            // Cập nhật vị trí và layout
                            val params = layoutParamsTextOverlay as WindowManager.LayoutParams

                            val targetY =
                                if ((params.y + ((state.captureRegion.bottom - state.captureRegion.top) * density / 2) > (state.captureRegion.top - 40) * density &&
                                    params.y + ((state.captureRegion.bottom - state.captureRegion.top) * density / 2) <= state.captureRegion.bottom * density))
                                {
                                    if((state.captureRegion.top - 40) * density < (state.captureRegion.bottom - state.captureRegion.top) * density / 2) {
                                        screenHeight - textOverlayView.height - minY
                                    }
                                    else {
                                        minY
                                    }
                                }
                                else if ((params.y  >= (state.captureRegion.top + state.captureRegion.bottom) * density / 2 &&
                                        params.y < state.captureRegion.bottom * density)) {
                                    if(screenHeight - state.captureRegion.bottom * density < (state.captureRegion.bottom - state.captureRegion.top) * density / 2) {
                                        minY
                                    }
                                    else {
                                        screenHeight - textOverlayView.height - minY
                                    }
                                }
                                else
                                {
                                    params.y
                                }

                            params.x += offsetX.toInt()
                            params.y += offsetY.toInt()

                            params.x = params.x.coerceIn((state.captureRegion.left * density).toInt(), (state.captureRegion.left * density).toInt())
                            params.y = layoutParamsTextOverlay.y.coerceIn(0, screenHeight - textOverlayView.height)
                            windowManager.updateViewLayout(textOverlayView, layoutParamsTextOverlay)

                            // Tạo hiệu ứng trượt mượt (smooth animation)
                            ValueAnimator.ofInt(params.y, targetY).apply {
                                duration = 300
                                interpolator = DecelerateInterpolator()
                                addUpdateListener {
                                    val value = it.animatedValue as Int
                                    params.y = value
                                    windowManager.updateViewLayout(textOverlayView, params)
                                }
                                start()
                            }
                        }
                    )
                }
            }
        }


        // Set LayoutParams for Floating Widget
        layoutParamsTextOverlay = WindowManager.LayoutParams(
//            widthPx,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
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
            gravity = Gravity.TOP or Gravity.START
        }

        // Thêm floatingView với layoutParamsCropArea mặc định (CropAreaAuto ban đầu)
        layoutParamsCropArea = WindowManager.LayoutParams(
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
        Log.d("AutoScreenModeService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParamsCropArea) // Thêm AutoArea/CropAreaAuto
            windowManager.addView(textOverlayView, layoutParamsTextOverlay) // Thêm TextOverlayAuto
            Log.d("AutoScreenModeService", "View added successfully")
        } catch (e: Exception) {
            Log.e("AutoScreenModeService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("AutoScreenModeService", "Service started")
        viewModel =
            ViewModelProvider(this@AutoScreenModeService)[AutoScreenModeViewModel::class.java]

        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        statusBarHeight = sharedPreferences.getInt("STATUS_BAR_HEIGHT", 0)
        Log.d("AutoScreenModeService", "StatusBar Height received: $statusBarHeight")

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
            Log.d("AutoScreenModeService", "Using existing MediaProjection instance.")
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AutoScreenModeService", "Service onDestroy called")

        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
                Log.d("AutoScreenModeService", "View removed successfully")
            } catch (e: Exception) {
                Log.e("AutoScreenModeService", "Error removing view: ${e.message}")
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
        Log.d("AutoScreenModeService", "StatusBar Height received: $statusBarHeight")

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
            Log.e("AutoScreenModeService", "MediaProjection is not initialized")
            stopSelf() // Dừng service nếu mediaProjection không hợp lệ
//            return
        }

        // Register the mediaProjection callback after initializing mediaProjection
        val mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("AutoScreenModeService", "MediaProjection stopped")
                // Giải phóng tài nguyên khi MediaProjection dừng
                releaseResources()
            }
        }
        MediaProjectionSingleton.mediaProjection?.registerCallback(
            mediaProjectionCallback,
            Handler(Looper.getMainLooper())
        )

        // Nếu VirtualDisplay đã tồn tại, chỉ cần cập nhật Surface mới
        if (VirtualDisplaySignleton.virtualDisplay != null) {
            // Cập nhật Surface cho VirtualDisplay thay vì tạo mới
            VirtualDisplaySignleton.virtualDisplay?.surface = imageReader.surface
        } else {
            // Nếu VirtualDisplay chưa tồn tại, tạo mới
            try {
                VirtualDisplaySignleton.virtualDisplay =
                    MediaProjectionSingleton.mediaProjection?.createVirtualDisplay(
                        "ScreenCapture",
                        width, height, d,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.surface, null, null
                    )
                if (VirtualDisplaySignleton.virtualDisplay == null) {
                    Log.e("AutoscreenModeService", "Failed to create virtual display")
                    releaseResources()
                    stopSelf() // Dừng service nếu không thể tạo virtual display
//                    return
                }
            } catch (e: Exception) {
                Log.e("AutoscreenModeService", "Error creating virtual display: ${e.message}")
                e.printStackTrace()
                //releaseResources()
                //stopSelf() // Dừng service nếu có lỗi
//                return
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

//            // Crop the image based on CaptureRegion
//            val croppedBitmap = Bitmap.createBitmap(
//                bitmap,
//                (captureRegion.left * density).toInt(),
//                (captureRegion.top * density + statusBarHeight).toInt(),
//                (captureRegion.right * density - captureRegion.left * density).toInt(),
//                (captureRegion.bottom * density - captureRegion.top * density).toInt()
//            )

            val x = (captureRegion.left * density).toInt()
            val y = (captureRegion.top * density + statusBarHeight).toInt()
            val width = ((captureRegion.right - captureRegion.left) * density).toInt()
            val height = ((captureRegion.bottom - captureRegion.top) * density).toInt()

            val safeX = x.coerceAtLeast(0)
            val safeY = y.coerceAtLeast(0)
            val safeWidth = width.coerceAtMost(bitmap.width - safeX)
            val safeHeight = height.coerceAtMost(bitmap.height - safeY)

            val croppedBitmap = try {
                Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
            } catch (e: IllegalArgumentException) {
                // Xử lý lỗi: ví dụ log và trả về bitmap gốc hoặc bitmap rỗng
                Log.e("AutoScreenModeService", "Crop bitmap failed: ${e.message}")
                bitmap // hoặc Bitmap.createBitmap(1,1,bitmap.config) nếu muốn bitmap rỗng
            }


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
            Log.d("AutoscreenModeService", "Resources released successfully")
        } catch (e: Exception) {
            Log.e("AutoscreenModeService", "Error releasing resources: ${e.message}")
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = recognizeTextFromImage(bitmap)
                Log.d("AutoScreenOCR", "Detected text: ${result.text}")
                viewModel.onAction(AutoScreenModeAction.OnChange(result))
                viewModel.onAction(AutoScreenModeAction.OnChangeTextVisibility(true))
            } catch (e: Exception) {
                Log.e("AutoScreenOCR", "Error: ${e.message}")
            }
        }
    }

    private fun startContinuousScreenshot(capture: Rect) {
        Log.d("AutoScreenModeService", "CaptureRegion: ${capture}")
        // Chạy chụp ảnh màn hình liên tục mỗi giây hoặc theo tần suất bạn muốn
        handler = Handler(Looper.getMainLooper())
        screenshotRunnable = object : Runnable {
            override fun run() {
                // Gọi startScreenshot để chụp ảnh màn hình
                startScreenshot(capture)

                // Tiếp tục gọi lại mỗi giây
                if (isPlaying) {
                    handler?.postDelayed(this, 1000)  // 1000ms = 1 giây
                }
            }
        }

        // Bắt đầu vòng lặp nếu isPlaying = true
        if (isPlaying) {
            handler?.post(screenshotRunnable!!)
        }
    }

    private fun stopContinuousScreenshot() {
        handler?.removeCallbacks(screenshotRunnable!!)  // Dừng vòng lặp nếu không còn chơi
        Log.d("AutoScreenModeService", "Stopped continuous screenshot")
    }


}