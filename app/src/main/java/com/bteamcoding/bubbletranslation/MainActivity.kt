package com.bteamcoding.bubbletranslation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bteamcoding.bubbletranslation.app.data.local.MediaProjectionPermissionHolder
import com.bteamcoding.bubbletranslation.app.presentation.MainScreen
import android.graphics.Rect
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import com.bteamcoding.bubbletranslation.core.utils.ScreenSizeHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service.PartialScreenModeService
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_CAPTURE_SCREEN = 1001

    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>

    private val permissionGrantedState = mutableStateOf(false)

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted
            // You can use the microphone here
        } else {
            // Request permission
            microphonePermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
        }

        // Lấy chiều rộng màn hình theo pixel
        val displayMetrics = resources.displayMetrics
        ScreenSizeHolder.screenWidth = displayMetrics.widthPixels.toFloat()

        setContent {
            MainScreen(
                onRequestScreenCapturePermission = {
                    val mediaProjectionManager =
                        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
                    startActivityForResult(screenCaptureIntent, REQUEST_CODE_CAPTURE_SCREEN)
                },
                onPermissionGranted = {
                    permissionGrantedState.value = true
                },
                permissionGranted = permissionGrantedState
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAPTURE_SCREEN && resultCode == RESULT_OK && data != null) {
            MediaProjectionPermissionHolder.resultCode = resultCode
            MediaProjectionPermissionHolder.resultData = data
            permissionGrantedState.value = true

            // Lưu statusBarHeight vào SharedPreferences
            val statusBarHeight = getStatusBarHeight()
            val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("STATUS_BAR_HEIGHT", statusBarHeight)
            editor.apply()

            Toast.makeText(this, "Đã cấp quyền ghi màn hình", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Từ chối quyền ghi màn hình", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getStatusBarHeight(): Int {
        val rectangle = Rect()  // Sử dụng Rect từ android.graphics
        val window = window  // Truy cập window của Activity
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }

    private val microphonePermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, you can proceed with microphone operations
                Toast.makeText(this, "Đã cấp quyền ghi âm", Toast.LENGTH_SHORT).show()
            } else {
                // Permission is not granted, inform the user
                Toast.makeText(
                    this,
                    "Từ chối quyền ghi âm, bạn sẽ không sử dụng được chức năng dịch audio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}