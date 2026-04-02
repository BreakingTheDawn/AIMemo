package com.aimemo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ========================================
// 颜色定义
// ========================================

// 主色调 - 蓝色系
val Primary = Color(0xFF1976D2)
val PrimaryVariant = Color(0xFF1565C0)
val OnPrimary = Color.White

// 次要色 - 青色系
val Secondary = Color(0xFF00ACC1)
val SecondaryVariant = Color(0xFF00838F)
val OnSecondary = Color.White

// 背景色
val Background = Color(0xFFF5F5F5)
val Surface = Color.White
val OnBackground = Color(0xFF1C1B1F)
val OnSurface = Color(0xFF1C1B1F)

// 深色主题
val DarkPrimary = Color(0xFF90CAF9)
val DarkSecondary = Color(0xFF80DEEA)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnPrimary = Color(0xFF1C1B1F)
val DarkOnSecondary = Color(0xFF1C1B1F)
val DarkOnBackground = Color.White
val DarkOnSurface = Color.White

// 优先级颜色
val PriorityHigh = Color(0xFFE53935)
val PriorityMedium = Color(0xFFFFA726)
val PriorityLow = Color(0xFF66BB6A)

// ========================================
// 主题定义
// ========================================

/**
 * 浅色主题配色方案
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB3261E),
    onError = Color.White
)

/**
 * 深色主题配色方案
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = Primary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = Secondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

/**
 * AI Memo 应用主题
 * 支持浅色/深色模式切换
 * @param darkTheme 是否使用深色主题
 */
@Composable
fun AIMemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 根据系统设置选择配色方案
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // 获取当前视图
    val view = LocalView.current
    
    // 设置状态栏颜色
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 应用主题
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
