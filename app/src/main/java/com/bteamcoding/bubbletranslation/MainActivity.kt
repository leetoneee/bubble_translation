package com.bteamcoding.bubbletranslation

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bteamcoding.bubbletranslation.app.data.local.MediaProjectionPermissionHolder
import com.bteamcoding.bubbletranslation.app.presentation.MainScreen
import com.bteamcoding.bubbletranslation.core.utils.translateText

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_CAPTURE_SCREEN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Tạo một intent yêu cầu quyền ghi màn hình
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()

        // Gọi yêu cầu quyền ghi màn hình
        startActivityForResult(screenCaptureIntent, REQUEST_CODE_CAPTURE_SCREEN)

        setContent {
            MainScreen()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAPTURE_SCREEN && resultCode == RESULT_OK && data != null) {
            MediaProjectionPermissionHolder.resultCode = resultCode
            MediaProjectionPermissionHolder.resultData = data

            Toast.makeText(this, "Đã cấp quyền ghi màn hình", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Từ chối quyền ghi màn hình", Toast.LENGTH_SHORT).show()
        }
    }
}