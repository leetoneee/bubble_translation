package com.bteamcoding.bubbletranslation.feature_camera.presentation.activity

import android.R.attr
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bteamcoding.bubbletranslation.MainActivity
import com.bteamcoding.bubbletranslation.app.presentation.BaseActivity
import com.bteamcoding.bubbletranslation.core.utils.recognizeTextFromImage
import com.bteamcoding.bubbletranslation.core.utils.translateVisionText
import com.bteamcoding.bubbletranslation.feature_camera.navigation.PreviewImageNavRoutes
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageAction
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageScreen
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageState
import com.bteamcoding.bubbletranslation.feature_camera.presentation.PreviewImageViewModel
import com.bteamcoding.bubbletranslation.feature_camera.presentation.TranslatedResultScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale


class PreviewImageActivity : BaseActivity() {
    private lateinit var viewModel: PreviewImageViewModel
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.US)
//                tts.setLanguage(Locale("vi", "VN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("TTS", "Speech started")
            }

            override fun onDone(utteranceId: String?) {
                Log.d("TTS", "Speech done")
                // TTS đã kết thúc đọc
                viewModel.onAction(PreviewImageAction.OnChangeIsSpeaking(false))
            }

            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Speech error")
                viewModel.onAction(PreviewImageAction.OnChangeIsSpeaking(false))
            }
        })


//        supportActionBar?.hide() // Ẩn ActionBar

        setContent {
            val navController = rememberNavController()
            viewModel = viewModel()
            val state: PreviewImageState by viewModel.state.collectAsStateWithLifecycle()

            NavHost(
                navController = navController,
                startDestination = PreviewImageNavRoutes.PREVIEW
            ) {
                composable(PreviewImageNavRoutes.PREVIEW) {
                    val imageUriString = intent.getStringExtra("imageUri")
                    val capturedImageUriString = intent.getStringExtra("capturedImageUri")
                    val imageUri = remember { imageUriString?.let { Uri.parse(it) } }
                    val capturedImageUri =
                        remember { capturedImageUriString?.let { Uri.parse(it) } }

                    var painter: AsyncImagePainter? by remember { mutableStateOf(null) }
                    var bitmapToProcess: Bitmap? by remember { mutableStateOf(null) }

                    //Use Coil to display the selected image
                    if (capturedImageUri != null) {
                        val file = File(capturedImageUri.path ?: "")
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(file)
                                .crossfade(true)
                                .allowHardware(false)
                                .build()
                        )
                    } else {
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = imageUri)
                                .crossfade(true)
                                .allowHardware(false)
                                .build()
                        )
                    }


                    LaunchedEffect(painter?.state) {
                        val painterState = painter?.state
                        if (painterState is AsyncImagePainter.State.Success) {
                            val drawable = painterState.result.drawable
                            drawable.let {
                                val bitmap = it.toBitmap()
                                if (bitmap != bitmapToProcess) {
                                    bitmapToProcess = bitmap
                                }
                            }
                        }
                    }

                    // Chỉ gọi OCR khi bitmapToProcess thay đổi
                    LaunchedEffect(bitmapToProcess) {
                        bitmapToProcess?.let { bitmap ->
                            // gọi OCR async ở đây, ví dụ gọi viewmodel hoặc launch coroutine
                            viewModel.onAction(PreviewImageAction.SetImageBitmap(bitmap))
                            processBitmap(bitmap)
                        }
                    }

                    PreviewImageScreen(
                        painter = painter,
                        visionText = state.translatedVisionText,
                        bitmap = state.imageBitmap,
                        isTextVisibility = state.isTextVisibility,
                        onBack = {
                            viewModel.onAction(PreviewImageAction.OnReset)
                            finish()
                        },
                        onViewResultText = {
                            navController.navigate(PreviewImageNavRoutes.RESULT)
                        },
                        onCompareResult = {
                            viewModel.onAction(PreviewImageAction.OnChangeTextVisibility(it))
                        }
                    )
                }

                composable(PreviewImageNavRoutes.RESULT) {
                    state.translatedVisionText?.let {
                        TranslatedResultScreen(
                            text = it,
                            isSpeaking = state.isSpeaking,
                            onBack = {
                                viewModel.onAction(PreviewImageAction.OnChangeIsSpeaking(false))
                                tts.stop()
                                navController.popBackStack()
                            },
                            onShare = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        state.translatedVisionText!!.textBlocks.joinToString("\n") { block -> block.translatedText })
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            },
                            onCopyText = { textCopied ->
                                val clipboard: ClipboardManager = getSystemService(
                                    CLIPBOARD_SERVICE
                                ) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("", textCopied))
                            },
                            onSpeak = { textSpeak ->
                                viewModel.onAction(PreviewImageAction.OnChangeIsSpeaking(true))
                                speak(textSpeak)
                            },
                            onStopSpeaking = {
                                viewModel.onAction(PreviewImageAction.OnChangeIsSpeaking(false))
                                tts.stop()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

    private fun processBitmap(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = recognizeTextFromImage(bitmap)
                Log.d("PreviewImageActivity OCR", "Detected text: ${result.text}")

                val translatedResult = translateVisionText(result)
                viewModel.onAction(PreviewImageAction.OnChange(result))
                viewModel.onAction(PreviewImageAction.OnChangeTranslatedVisionText(translatedResult))
                viewModel.onAction(PreviewImageAction.OnChangeTextVisibility(true))
            } catch (e: Exception) {
                Log.e("PreviewImageActivity OCR", "Error: ${e.message}")
            }
        }
    }
}
