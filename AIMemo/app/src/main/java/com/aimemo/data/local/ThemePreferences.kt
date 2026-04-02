package com.aimemo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 主题偏好存储类
 * 使用DataStore保存用户的主题选择偏好
 */

// DataStore 扩展属性
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

object ThemePreferences {

    // 主题模式键
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    /**
     * 获取主题模式
     * 返回值： "light"（浅色）、"dark"（深色）、"system"（跟随系统）
     */
    fun getThemeMode(context: Context): Flow<String> {
        return context.themeDataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY] ?: "system"
        }
    }

    /**
     * 设置主题模式
     * @param context 上下文
     * @param mode 主题模式："light"、"dark"、"system"
     */
    suspend fun setThemeMode(context: Context, mode: String) {
        context.themeDataStore.edit { preferences ->
            if (mode == "system") {
                // 跟随系统设置，删除存储的偏好
                preferences.remove(THEME_MODE_KEY)
            } else {
                preferences[THEME_MODE_KEY] = mode
            }
        }
    }

    /**
     * 主题模式常量
     */
    object ThemeMode {
        const val LIGHT = "light"
        const val DARK = "dark"
        const val SYSTEM = "system"
    }
}
