package com.aimemo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * API密钥偏好存储类
 * 使用DataStore保存用户的API密钥，实现持久化存储
 */

// DataStore 扩展属性
private val Context.apiKeyDataStore: DataStore<Preferences> by preferencesDataStore(name = "api_key_preferences")

object ApiKeyPreferences {

    // API密钥存储键
    private val API_KEY = stringPreferencesKey("api_key")

    /**
     * 获取API密钥
     * @param context 上下文
     * @return API密钥流，如果未设置则返回空字符串
     */
    fun getApiKey(context: Context): Flow<String> {
        return context.apiKeyDataStore.data.map { preferences ->
            preferences[API_KEY] ?: ""
        }
    }

    /**
     * 设置API密钥
     * @param context 上下文
     * @param apiKey API密钥
     */
    suspend fun setApiKey(context: Context, apiKey: String) {
        context.apiKeyDataStore.edit { preferences ->
            if (apiKey.isBlank()) {
                // 如果密钥为空，删除存储的偏好
                preferences.remove(API_KEY)
            } else {
                preferences[API_KEY] = apiKey
            }
        }
    }

    /**
     * 清除API密钥
     * @param context 上下文
     */
    suspend fun clearApiKey(context: Context) {
        context.apiKeyDataStore.edit { preferences ->
            preferences.remove(API_KEY)
        }
    }
}
