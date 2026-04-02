package com.aimemo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ========================================
// Seed Color - Indigo (靛蓝)
// Material You 动态配色的基础色
// ========================================
private val SEED_COLOR = Color(0xFF3F51B5)

// ========================================
// 业务色（优先级）— 保持不变
// 用于日程卡片左侧优先级标识和标签
// ========================================

/** 高优先级 - 红色 */
val PriorityHigh = Color(0xFFF44336)

/** 中优先级 - 橙色 */
val PriorityMedium = Color(0xFFFF9800)

/** 低优先级 - 蓝色 */
val PriorityLow = Color(0xFF2196F3)

// ========================================
// 静态回退配色方案（Android < 12 使用）
// 基于 Indigo 色板精心调校，保持 AI 科技感
// ========================================

/**
 * 浅色主题静态配色方案
 * 适用于 Android 12 以下设备或禁用动态配色时
 * 主色调为 Indigo 紫蓝色系，营造专业科技感
 */
private val LightFallbackColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF3025A0),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4A048E),
    tertiary = Color(0xFF0891B2),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFFAFE),
    onTertiaryContainer = Color(0xFF002028),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF1E293B),
    surface = Color.White,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF1F5F9),
    inversePrimary = Color(0xFFA5B4FC)
)

/**
 * 深色主题静态配色方案
 * 适用于 Android 12 以下设备的深色模式
 * 降低整体亮度，使用柔和的紫蓝渐变
 */
private val DarkFallbackColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFC7D2FE),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEAEBFF),
    tertiary = Color(0xFF22D3EE),
    onTertiary = Color(0xFF164E63),
    tertiaryContainer = Color(0xFF0D4F5C),
    onTertiaryContainer = Color(0xFFA5F1FF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFECACA),
    inverseSurface = Color(0xFFF1F5F9),
    inverseOnSurface = Color(0xFF1E293B),
    inversePrimary = Color(0xFF4F46E5)
)

/**
 * AI Memo 应用主题
 *
 * Android 12+ 设备自动启用 Material You 动态配色（dynamicLightColorScheme），
 * 以 Indigo 为 Seed Color 自动生成完整色板。
 * 低版本设备回退到精心设计的静态 Indigo 配色方案。
 *
 * @param darkTheme 是否使用深色主题，默认跟随系统设置
 * @param dynamicColor 是否启用动态配色，默认 true（Android 12+ 生效）
 * @param content 内容 Composable 函数
 */
@Composable
fun AIMemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 根据设备能力和用户偏好选择配色方案
    val colorScheme = when {
        // Android 12 (S) 及以上 + 启用动态配色 → Material You 动态取色
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // 低版本或动态配色禁用 → 使用静态回退方案
        darkTheme -> DarkFallbackColorScheme
        else -> LightFallbackColorScheme
    }

    // 获取当前视图引用，用于配置系统栏颜色
    val view = LocalView.current

    // 仅在非编辑模式（真实运行时）应用系统栏配置
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 状态栏颜色跟随 surface 色，实现透明沉浸效果
            window.statusBarColor = colorScheme.surface.toArgb()
            // 根据主题明暗调整状态栏图标颜色
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 应用完整的 Material 3 主题
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
