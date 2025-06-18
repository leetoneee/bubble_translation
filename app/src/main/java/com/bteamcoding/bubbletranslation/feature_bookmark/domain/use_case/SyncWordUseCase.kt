package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordDto
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.WordRepository
import javax.inject.Inject

class SyncWordUseCase @Inject constructor(private val repo: WordRepository) {
    suspend operator fun invoke(
        lastSyncTime: Long,
        words: List<WordDto>,
        userId: Long
    ): ApiResponse<WordSyncResponse> {
        val dto = WordSyncRequest(lastSyncTime, words)
        return repo.syncWord(dto, userId)
    }
}