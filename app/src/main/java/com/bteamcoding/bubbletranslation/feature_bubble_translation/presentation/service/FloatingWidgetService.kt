package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
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
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.DraggableFloatingWidget

class FloatingWidgetService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    // Thêm biến theo dõi vị trí
    private var initialX = 0
    private var initialY = 0

    override fun onCreate() {
        super.onCreate()
        Log.d("FloatingWidgetService", "Service onCreate called")

        // Initialize SavedStateRegistry
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // Khởi tạo vị trí ban đầu
        initialX = 100 // Vị trí x ban đầu
        initialY = 200 // Vị trí y ban đầu

        // Create ComposeView for Floating Widget
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingWidgetService)
            setViewTreeViewModelStoreOwner(this@FloatingWidgetService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWidgetService)

            // Log để kiểm tra ComposeView được tạo
            Log.d("FloatingWidgetService", "Creating ComposeView")

            setContent {
                val viewModel: FloatingWidgetViewModel = ViewModelProvider(this@FloatingWidgetService)[FloatingWidgetViewModel::class.java]
                val state by viewModel.state.collectAsState()

                DraggableFloatingWidget(
                    isExpanded = state.isExpanded,
                    onClose = {
                        viewModel.onAction(FloatingWidgetAction.OnClose)
                        stopSelf()
                    },
                    onToggleExpand = { viewModel.onAction(FloatingWidgetAction.OnToggleExpand) },
                    onDrag = { offsetX, offsetY ->
                        // Cập nhật vị trí và layout
                        val params = layoutParams as WindowManager.LayoutParams

                        params.x += offsetX.toInt()
                        params.y += offsetY.toInt()
                        windowManager.updateViewLayout(floatingView, layoutParams)

                        // Log vị trí mới
//                        Log.d("FloatingWidgetService", "Drag - New position: x=${layoutParams.width}, y=${layoutParams.height}")
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
        Log.d("FloatingWidgetService", "Adding view to WindowManager")

        // Add Floating Widget to WindowManager
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.addView(floatingView, layoutParams)
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
}
