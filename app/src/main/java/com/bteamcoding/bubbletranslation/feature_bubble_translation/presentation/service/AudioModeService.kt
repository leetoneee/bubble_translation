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
import android.hardware.display.VirtualDisplay
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
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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
import com.bteamcoding.bubbletranslation.core.utils.SpeechRecognizerHelper
import com.bteamcoding.bubbletranslation.core.utils.VirtualDisplaySignleton
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AudioModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AudioModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.CoatingLayer
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.SubtitleOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.abs

class AudioModeService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    val resultCode = MediaProjectionPermissionHolder.resultCode
    val resultData = MediaProjectionPermissionHolder.resultData

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private lateinit var speechRecognizerHelper: SpeechRecognizerHelper

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var viewModel: AudioModeViewModel

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    // Thêm biến theo dõi vị trí
    private var initialX = 0
    private var initialY = 0
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()

        speechRecognizerHelper = SpeechRecognizerHelper(this) {
            Log.d("AudioModeService", "Recognized: $it")
            viewModel.onAction(AudioModeAction.OnChangeText(it))
        }

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
        Log.d("AudioModeService", "Service is now running in the foreground")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@AudioModeService)
            setViewTreeViewModelStoreOwner(this@AudioModeService)
            setViewTreeSavedStateRegistryOwner(this@AudioModeService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("AudioModeService", "Creating ComposeView")

            setContent {
                viewModel =
                    ViewModelProvider(this@AudioModeService)[AudioModeViewModel::class.java]
                val state by viewModel.state.collectAsState()
                val params = layoutParams as WindowManager.LayoutParams

                SubtitleOverlay(
                    isRecognizing = state.isRecognizing,
                    subtitleText = state.recognizedText,
                    onStartRecognition = {
                        mediaProjection =
                            mediaProjectionManager.getMediaProjection(resultCode, resultData!!)
                        mediaProjection?.let {
                            speechRecognizerHelper.startRecognitionFromMediaProjection(it)
                        }
                        viewModel.onAction(AudioModeAction.OnChangeIsRecognizing(true))
                    },
                    onStopRecognition = {
                        speechRecognizerHelper.stopRecognition()
                        mediaProjection?.stop()
                        viewModel.onAction(AudioModeAction.OnChangeIsRecognizing(false))
                    }
                )
            }
        }

        // Set LayoutParams for Floating Widget
        layoutParams = WindowManager.LayoutParams(
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
            x = initialX
            y = initialY
            gravity = Gravity.TOP or Gravity.START
        }

        // Log để kiểm tra WindowManager
        Log.d("AudioModeService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParams)
            Log.d("AudioModeService", "View added successfully")
        } catch (e: Exception) {
            Log.e("AudioModeService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("AudioModeService", "Service started")

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
            Log.d("AudioModeService", "Using existing MediaProjection instance.")
        }

        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AudioModeService", "Service onDestroy called")

        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
                Log.d("AudioModeService", "View removed successfully")
            } catch (e: Exception) {
                Log.e("AudioModeService", "Error removing view: ${e.message}")
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
    }