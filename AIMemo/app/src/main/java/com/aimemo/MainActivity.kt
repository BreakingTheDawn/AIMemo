package com.aimemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimemo.ui.screens.MainScreen
import com.aimemo.ui.theme.AIMemoTheme
import com.aimemo.ui.viewmodel.MainViewModel

/**
 * 主Activity - 应用程序入口点
 * 使用Jetpack Compose构建UI
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用ViewModelProvider初始化ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        // 启用边到边显示，适配现代Android设备
        enableEdgeToEdge()
        
        setContent {
            // 获取当前主题模式
            val themeMode by viewModel.themeMode.collectAsState()
            
            // 计算是否使用深色主题
            val useDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            
            // 应用主题包装
            AIMemoTheme(darkTheme = useDarkTheme) {
                // 主表面容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 主屏幕内容
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
