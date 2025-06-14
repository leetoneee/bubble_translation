package com.bteamcoding.bubbletranslation.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore @Inject constructor(private val context: Context) {
    private val dataStore = context.dataStore

    private val ID_KEY = longPreferencesKey("id")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val EMAIL_KEY = stringPreferencesKey("email")

    suspend fun saveUserInfo(id: Long, username: String, email: String) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = id
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email
        }
    }

    val userInfo: Flow<User> = dataStore.data
        .map { preferences ->
            val id = preferences[ID_KEY] ?: 0
            val username = preferences[USERNAME_KEY] ?: ""
            val email = preferences[EMAIL_KEY] ?: ""
            User(id, username, email, "")
        }

    suspend fun clearUserInfo() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}