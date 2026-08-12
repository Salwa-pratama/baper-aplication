package com.example.baper_andoid.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_NAME = stringPreferencesKey("user_name")

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    /** Baca token sekali saja (dipakai OkHttp interceptor, bukan Flow). */
    suspend fun getAuthTokenOnce(): String? = authToken.first()

    suspend fun getRefreshTokenOnce(): String? = refreshToken.first()

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    /** Simpan hasil login: access token + refresh token + identitas user. */
    suspend fun saveSession(
        accessToken: String,
        refreshToken: String?,
        userId: String?,
        userName: String?
    ) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = accessToken
            refreshToken?.let { preferences[REFRESH_TOKEN] = it }
            userId?.let { preferences[USER_ID] = it }
            userName?.let { preferences[USER_NAME] = it }
        }
    }

    /** Dipakai setelah refresh token berhasil ditukar. */
    suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = accessToken
            refreshToken?.let { preferences[REFRESH_TOKEN] = it }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
