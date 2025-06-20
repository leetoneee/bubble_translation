package com.bteamcoding.bubbletranslation.app.data.repository

import com.bteamcoding.bubbletranslation.app.data.local.UserDataStore
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDataStore: UserDataStore
) {
    suspend fun saveUserInfo(id: Long, username: String, email: String) {
        userDataStore.saveUserInfo(id, username, email)
    }

    fun getUserInfo(): Flow<User> {
        return userDataStore.userInfo
    }

    suspend fun logout() {
        userDataStore.clearUserInfo()
    }
}