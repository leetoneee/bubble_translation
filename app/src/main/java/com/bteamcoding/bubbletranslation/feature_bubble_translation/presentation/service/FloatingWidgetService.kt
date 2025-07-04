package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bteamcoding.bubbletranslation.app.data.local.MediaProjectionPermissionHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StopFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.ChooseLanguageScreen
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.DraggableFloatingWidget
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.PartialScreenModeState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.DraggableTranslateWord
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class FloatingWidgetService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val resultCode = MediaProjectionPermissionHolder.resultCode
    private val resultData = MediaProjectionPermissionHolder.resultData

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var floatingLayoutParams: WindowManager.LayoutParams

    private var state by mutableStateOf(PartialScreenModeState())

    private lateinit var chooseLanguageLayoutParams: WindowManager.LayoutParams

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    // Thêm biến theo dõi vị trí
    private var initialX = 0
    private var initialY = 0
    private val minX = 0
    private val minY = 0
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private val midX = screenWidth / 2

    override fun onCreate() {
        super.onCreate()
        Log.d("FloatingWidgetService", "Service onCreate called")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Khởi tạo vị trí ban đầu
        initialX = minX // Vị trí x ban đầu
        initialY = 200 // Vị trí y ban đầu

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingWidgetService)
            setViewTreeViewModelStoreOwner(this@FloatingWidgetService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWidgetService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("FloatingWidgetService", "Creating ComposeView")

            setContent {
//                val viewModel: FloatingWidgetViewModel =
//                    ViewModelProvider(this@FloatingWidgetService)[FloatingWidgetViewModel::class.java]
                val viewModel = FloatingWidgetViewModelHolder.instance
                // Khi Service được khởi động, chuyển trạng thái isOn sang true

                val state by viewModel.state.collectAsState()
                val stopFWUseCase = StopFloatingWidgetUseCase(LocalContext.current)
                var showLanguageScreen by remember { mutableStateOf(false) }
                var showBeeTranslate by remember { mutableStateOf(false) }

                floatingLayoutParams = WindowManager.LayoutParams(
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
                    x = 0
                    y = 0
                    gravity = Gravity.TOP or Gravity.START
                }

                chooseLanguageLayoutParams = WindowManager.LayoutParams(
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
                    x = 0 // Đặt vị trí x là 0 để luôn hiển thị ở góc trái
                    y = 0 // Đặt vị trí y là 0 để luôn hiển thị ở góc trên cùng
                    gravity = Gravity.TOP or Gravity.START // Căn góc trái trên cùng
                }

                LaunchedEffect(showLanguageScreen) {
                    val params =
                        if (showLanguageScreen) chooseLanguageLayoutParams else floatingLayoutParams
                    try {
                        windowManager.updateViewLayout(floatingView, params)
                    } catch (e: Exception) {
                        Log.e("FloatingWidgetService", "Error updating layout params: ${e.message}")
                    }
                }

                // Khi showLanguageScreen là true, ẩn floatingView và hiển thị ChooseLanguageScreen
                if (showLanguageScreen) {
                    ChooseLanguageScreen(
                        state = state,
                        onUpdateSourceLanguage = { newSourceLanguage ->
                            viewModel.onAction(
                                FloatingWidgetAction.OnSourceLanguageChange(
                                    newSourceLanguage
                                )
                            )
                        },
                        onUpdateTargetLanguage = { newTargetLanguage ->
                            viewModel.onAction(
                                FloatingWidgetAction.OnTargetLanguageChange(
                                    newTargetLanguage
                                )
                            )
                        },
                        onShowLanguageScreenChanged = {
                            // Cập nhật showLanguageScreen trong service
                            showLanguageScreen = false  // Đổi về false khi nhấn xác nhận
                        }
                    ) // Hiển thị ChooseLanguageScreen với size chiếm toàn bộ màn hình

                } else {
                    if (showBeeTranslate) {
                        DraggableTranslateWord(
                            state = state,
                            onDrag = { offsetX, offsetY ->
                                // Cập nhật vị trí và layout
                                val params = layoutParams as WindowManager.LayoutParams

                                val maxY = screenHeight - floatingView.height - minY
                                val maxX = screenWidth - floatingView.width - minX

                                params.x += offsetX.toInt()
                                params.y += offsetY.toInt()
                                params.y = params.y.coerceIn(minY, maxY)
                                params.x = params.x.coerceIn(minX, maxX)
                                windowManager.updateViewLayout(floatingView, layoutParams)
                            },
                            onDragEnd = {
                                val params = layoutParams as WindowManager.LayoutParams

                                val targetX = 0

                                // Tạo hiệu ứng trượt mượt (smooth animation)
                                ValueAnimator.ofInt(params.x, targetX).apply {
                                    duration = 300
                                    interpolator = DecelerateInterpolator()
                                    addUpdateListener {
                                        val value = it.animatedValue as Int
                                        params.x = value
                                        windowManager.updateViewLayout(floatingView, params)
                                    }
                                    start()
                                }

                                params.alpha = 0f
                                windowManager.updateViewLayout(floatingView, params)
                                Log.d("DraggableTranslateWord", "params.x: ${params.x}")
                                Log.d("DraggableTranslateWord", "params.y: ${params.y}")

                                lifecycleScope.launch {
                                    handleTranslateWord(params) // Gọi hàm trước

                                    delay(2000) // Chờ 2 giây (2000 milliseconds)

                                    params.alpha = 1f
                                    windowManager.updateViewLayout(floatingView, params)
                                }

//                                handleTranslateWord(params)
//
//                                params.alpha = 1f
//                                windowManager.updateViewLayout(floatingView, params)
                            },
                            onShowBeeTranslateChanged = { showBeeTranslate = false }
                        )
                    } else {
                        DraggableFloatingWidget(
                            state = state,
                            onClose = {
                                viewModel.onAction(FloatingWidgetAction.OnClose)
                                stopFWUseCase()
                                stopSelf()
                            },
                            onToggleExpand = {
                                viewModel.onAction(FloatingWidgetAction.OnToggleExpand)
                                val params = layoutParams as WindowManager.LayoutParams

                                if (!state.isExpanded) {
                                    initialX = params.x
                                    initialY = params.y

                                    params.x = minX
                                    params.y = minY
                                    windowManager.updateViewLayout(floatingView, layoutParams)
                                } else {
                                    params.x = initialX
                                    params.y = initialY
                                    windowManager.updateViewLayout(floatingView, layoutParams)
                                }
                            },
                            onModeChange = {
                                viewModel.onAction(FloatingWidgetAction.OnModeChange(it))
                            },
                            onDrag = { offsetX, offsetY ->
                                // Cập nhật vị trí và layout
                                val params = layoutParams as WindowManager.LayoutParams

                                val maxY = screenHeight - floatingView.height - minY

                                params.x += offsetX.toInt()
                                params.y += offsetY.toInt()
                                params.y = params.y.coerceIn(minY, maxY)
                                windowManager.updateViewLayout(floatingView, layoutParams)


                                // Log vị trí mới
//                        Log.d("FloatingWidgetService", "Drag - New position: x=${layoutParams.width}, y=${layoutParams.height}")
                            },
                            onDragEnd = {
                                val params = layoutParams as WindowManager.LayoutParams

                                val targetX = if (params.x < midX) {
                                    minX // về bên trái
                                } else {
                                    screenWidth - floatingView.width - minX// về bên phải
                                }

                                // Tạo hiệu ứng trượt mượt (smooth animation)
                                ValueAnimator.ofInt(params.x, targetX).apply {
                                    duration = 300
                                    interpolator = DecelerateInterpolator()
                                    addUpdateListener {
                                        val value = it.animatedValue as Int
                                        params.x = value
                                        windowManager.updateViewLayout(floatingView, params)
                                    }
                                    start()
                                }
                            },
                            onClick = {
                                handleTranslateService(state.translateMode)
                            },
                            onShowLanguageScreenChanged = { showLanguageScreen = true },
                            onShowBeeTranslateChanged = { showBeeTranslate = true }
                        )
                    }
                }
            }
        }

        // Set LayoutParams for Floating Widget
        floatingLayoutParams = WindowManager.LayoutParams(
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
        Log.d("FloatingWidgetService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, floatingLayoutParams)
            Log.d("FloatingWidgetService", "View added successfully")
        } catch (e: Exception) {
            Log.e("FloatingWidgetService", "Error adding view: ${e.message}")
        }

        // Update Lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FloatingWidgetService", "Service onDestroy called")

        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
                Log.d("FloatingWidgetService", "View removed successfully")
            } catch (e: Exception) {
                Log.e("FloatingWidgetService", "Error removing view: ${e.message}")
            }
        }

        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Implement LifecycleOwner
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    // Implement ViewModelStoreOwner
    override val viewModelStore: ViewModelStore
        get() = viewModelStoreInstance

    // Implement SavedStateRegistryOwner
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private fun handleTranslateService(translateMode: TranslateMode) {
        val resultCode = MediaProjectionPermissionHolder.resultCode
        val resultData = MediaProjectionPermissionHolder.resultData

        when (translateMode) {
            TranslateMode.FULLSCREEN -> {
                Log.i("Translate Service", "Full screen mode")

                if (resultData != null) {
                    val intent = Intent(this, FullscreenModeService::class.java).apply {
                        putExtra("resultCode", resultCode)
                        putExtra("resultData", resultData)
                    }
                    startService(intent)
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            TranslateMode.CROP -> {
                Log.i("Translate Service", "Crop screen mode")
                if (resultData != null) {
                    Log.i("Translate Service", "Crop screen mode in")

                    val intentCrop = Intent(this, PartialScreenModeService::class.java).apply {
                        putExtra("resultCode", resultCode)
                        putExtra("resultData", resultData)
                    }
                    startService(intentCrop)
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            TranslateMode.AUTO -> {
                Log.i("Translate Service", "Auto mode")
                if (resultData != null) {
                    val intentCrop = Intent(this, AutoScreenModeService::class.java).apply {
                        putExtra("resultCode", resultCode)
                        putExtra("resultData", resultData)
                    }
                    startService(intentCrop)
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            TranslateMode.AUDIO -> {
                Log.i("Translate Service", "Audio mode")
                if (resultData != null) {
                    Log.i("Translate Service", "Crop screen mode in")

                    val intentCrop = Intent(this, AudioModeService::class.java).apply {
                        putExtra("resultCode", resultCode)
                        putExtra("resultData", resultData)
                    }
                    startService(intentCrop)
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            TranslateMode.WORD -> {
                Log.i("Translate Service", "Word mode")
                if (resultData != null) {
                    Log.i("Translate Service", "Word on screen mode in")

                    val intentCrop = Intent(this, WordScreenModeService::class.java).apply {
                        putExtra("resultCode", resultCode)
                        putExtra("resultData", resultData)
                    }
                    startService(intentCrop)
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleTranslateWord(
        params: WindowManager.LayoutParams
    ) {
        val resultCode = MediaProjectionPermissionHolder.resultCode
        val resultData = MediaProjectionPermissionHolder.resultData

        Log.i("Translate Service", "Word mode")
        if (resultData != null) {
            Log.i("Translate Service", "Word on screen mode in")

            val intentCrop = Intent(this, WordScreenModeService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("resultData", resultData)
                putExtra("params_x", params.x)
                putExtra("params_y", params.y)
            }
            startService(intentCrop)
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền ghi màn hình trước", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
