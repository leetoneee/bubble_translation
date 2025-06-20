package com.bteamcoding.bubbletranslation.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.toLowerCase
import androidx.core.app.ActivityCompat
import com.bteamcoding.bubbletranslation.core.model.VoskModelManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.vosk.Recognizer

class SpeechRecognizerHelper(
    private val context: Context,
    private val country: Country,
    var onResult: (String) -> Unit
) {
    private var modelManager = VoskModelManager(context)
    private lateinit var recognizer: Recognizer
    private var audioRecord: AudioRecord? = null
    private var recognizing = false
    private var recognitionJob: Job? = null
    private val batchSize: Int = 8

    // Thêm instance TextFilter, gọi onResult sau khi lọc
    private val textFilter = PartialTextProcessor(batchSize)

    fun startRecognition() {
        if (recognizing) return
        recognizing = true

        // Chọn mô hình dựa trên ngôn ngữ
        val assetModelName = when (country.countryIso.lowercase()) {
            "cn" -> "model-cn"  // Tiếng Trung
            "jp" -> "model-jp"  // Tiếng Nhật
            else -> "model-en"  // Tiếng Anh mặc định
        }

        modelManager.loadModelAsync().whenComplete { model, throwable ->
            if (throwable != null) {
                Log.e("SpeechRecognizerHelper", "Error loading model", throwable)
                recognizing = false
                return@whenComplete
            }

            try {
                recognizer = Recognizer(model, 16000.0f)

                val bufferSize = AudioRecord.getMinBufferSize(
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("SpeechRecognizerHelper", "Microphone permission not granted")
                    recognizing = false
                    return@whenComplete
                }

                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                audioRecord?.startRecording()
                val buffer = ByteArray(4096)

                recognitionJob = CoroutineScope(Dispatchers.IO).launch {
                    while (recognizing && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val len = audioRecord!!.read(buffer, 0, buffer.size)
                        if (len > 0 && recognizer.acceptWaveForm(buffer, len)) {
                            withContext(Dispatchers.Main) {
                                if (recognizer.acceptWaveForm(buffer, len)) {
                                    val result = recognizer.result
                                    Log.d("SpeechRecognizerHelper", "Recognized result: $result")
                                    onResult(result)
                                }
//                                onResult(recognizer.result)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("SpeechRecognizerHelper", "Error during recognition", e)
                recognizing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startRecognitionFromMediaProjection(mediaProjection: MediaProjection) {
        if (recognizing) return
        recognizing = true

        // Chọn mô hình dựa trên ngôn ngữ
        val assetModelName = when (country.countryIso.lowercase()) {
            "cn" -> "model-cn"  // Tiếng Trung
            "jp" -> "model-jp"  // Tiếng Nhật
            else -> "model-en"  // Tiếng Anh mặc định
        }

        modelManager.loadModelAsync(assetModelName = assetModelName).whenComplete { model, throwable ->
            if (throwable != null) {
                Log.e("SpeechRecognizerHelper", "Error loading model", throwable)
                recognizing = false
                return@whenComplete
            }

            try {
                recognizer = Recognizer(model, 16000.0f)

                val audioConfig = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                    .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(16000)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()

                val bufferSize = AudioRecord.getMinBufferSize(
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                if (ActivityCompat.checkSelfPermission(
                        context.applicationContext,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.e("SpeechRecognizerHelper", "Microphone permission not granted")
                    recognizing = false
                    return@whenComplete
                }

                audioRecord = AudioRecord.Builder()
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setAudioPlaybackCaptureConfig(audioConfig)
                    .build()

                audioRecord?.startRecording()
                val buffer = ByteArray(4096)

                recognitionJob = CoroutineScope(Dispatchers.IO).launch {
                    while (recognizing && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val len = audioRecord!!.read(buffer, 0, buffer.size)
                        if (len > 0) {
                            if (recognizer.acceptWaveForm(buffer, len)) {
                                withContext(Dispatchers.Main) {
                                    val result = recognizer.result
                                    val json = JSONObject(result)
                                    val text = json.optString("text")
                                    Log.i("Recognized Text result", result)
                                    if (text.isEmpty()) {
                                        textFilter.reset()
                                        onResult("")
                                    } else {
                                        val filteredText = textFilter.inputText(text)
                                        onResult(filteredText)
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    val partial = recognizer.partialResult
                                    val json = JSONObject(partial)
                                    val text = json.optString("partial")
                                    Log.i("Recognized Text partial", partial)
                                    if (text.isEmpty()) {
                                        textFilter.reset()
                                        onResult("")
                                    } else {
                                        val filteredText = textFilter.inputText(text)
                                        onResult(filteredText)
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("SpeechRecognizerHelper", "Error during recognition", e)
                recognizing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startRecognitionFromMediaProjectionAndTranslate(mediaProjection: MediaProjection) {
        if (recognizing) return
        recognizing = true

        // Chọn mô hình dựa trên ngôn ngữ
        val assetModelName = when (country.countryIso.lowercase()) {
            "cn" -> "model-cn"  // Tiếng Trung
            "jp" -> "model-jp"  // Tiếng Nhật
            else -> "model-en"  // Tiếng Anh mặc định
        }

        modelManager.loadModelAsync(assetModelName = assetModelName).whenComplete { model, throwable ->
            if (throwable != null) {
                Log.e("SpeechRecognizerHelper", "Error loading model", throwable)
                recognizing = false
                return@whenComplete
            }

            try {
                recognizer = Recognizer(model, 16000.0f)

                val audioConfig = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                    .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(16000)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()

                val bufferSize = AudioRecord.getMinBufferSize(
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                if (ActivityCompat.checkSelfPermission(
                        context.applicationContext,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("SpeechRecognizerHelper", "Microphone permission not granted")
                    recognizing = false
                    return@whenComplete
                }

                audioRecord = AudioRecord.Builder()
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setAudioPlaybackCaptureConfig(audioConfig)
                    .build()

                audioRecord?.startRecording()
                val buffer = ByteArray(4096)

                recognitionJob = CoroutineScope(Dispatchers.IO).launch {
                    while (recognizing && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val len = audioRecord!!.read(buffer, 0, buffer.size)
                        if (len > 0) {
                            if (recognizer.acceptWaveForm(buffer, len)) {
                                withContext(Dispatchers.Main) {
                                    val result = recognizer.result
                                    val json = JSONObject(result)
                                    val text = json.optString("text")
                                    Log.i("Recognized Text result", result)
                                    if (text.isEmpty()) {
                                        textFilter.reset()
                                        onResult("")
                                    } else {
                                        handlePartialRecognitionAndTranslate(text, true)
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    val partial = recognizer.partialResult
                                    val json = JSONObject(partial)
                                    val text = json.optString("partial")
                                    Log.i("Recognized Text partial", partial)
                                    if (text.isEmpty()) {
                                        textFilter.reset()
                                        onResult("")
                                    } else {
                                        handlePartialRecognitionAndTranslate(text, false)
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("SpeechRecognizerHelper", "Error during recognition", e)
                recognizing = false
            }
        }
    }

    private suspend fun handlePartialRecognitionAndTranslate(
        text: String,
        isResult: Boolean
    ) {
        val filteredText = textFilter.inputText(text)
        val words = filteredText.trim().split(Regex("\\s+"))
        val wordCount = words.size

        val halfBatch = batchSize / 2

        if (isResult) {
            onResult(translateText(filteredText))
        }

        // Nếu đạt mốc 2 * batchSize, chờ 500ms rồi dịch toàn bộ
        if (wordCount == 2 * batchSize) {
            onResult(translateText(filteredText))
        }

        if (wordCount >= halfBatch && wordCount % halfBatch == 0) {
            onResult(translateText(filteredText))
        }
    }

    fun stopRecognition() {
        recognizing = false
        recognitionJob?.cancel()
        recognitionJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
