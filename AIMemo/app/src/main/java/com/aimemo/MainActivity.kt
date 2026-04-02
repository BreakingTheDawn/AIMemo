package com.aimemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aimemo.ui.screens.MainScreen
import com.aimemo.ui.theme.AIMemoTheme

/**
 * 主Activity - 应用程序入口点
 * 使用Jetpack Compose构建UI
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用边到边显示，适配现代Android设备
        enableEdgeToEdge()
        
        setContent {
            // 应用主题包装
            AIMemoTheme {
                // 主表面容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 主屏幕内容
                    MainScreen()
                }
            }
        }
    }
}
