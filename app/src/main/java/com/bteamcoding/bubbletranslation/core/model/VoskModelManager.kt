package com.bteamcoding.bubbletranslation.core.model

import android.content.Context
import android.util.Log
import org.vosk.Model
import org.vosk.android.StorageService
import java.util.concurrent.CompletableFuture

class VoskModelManager(private val context: Context) {

    private var model: Model? = null
    private val TAG = "VoskModelManager"

    fun loadModelAsync(
        assetModelName: String = "model-en",
        cacheFolderName: String = "model"
    ): CompletableFuture<Model> {
        val future = CompletableFuture<Model>()

        if (model != null) {
            future.complete(model)
            return future
        }

        StorageService.unpack(
            context,
            assetModelName,
            cacheFolderName,
            { voskModel ->
                try {
                    model = voskModel
                    Log.i(TAG, "Model unpacked and loaded from: $model")
                    future.complete(model)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load model: ${e.message}", e)
                    future.completeExceptionally(e)
                }
            },
            { exception ->
                Log.e(TAG, "Failed to unpack model: ${exception.message}", exception)
                future.completeExceptionally(exception)
            }
        )
        return future
    }

    fun getModel(): Model? = model

    fun release() {
        model?.close()
        model = null
    }
}
