package com.aimemo

import android.app.Application

/**
 * AI Memo 应用程序入口类
 * 负责初始化全局组件和依赖注入容器
 */
class AIMemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化全局配置
        initApp()
    }

    /**
     * 初始化应用程序配置
     */
    private fun initApp() {
        // 预留：后续可添加依赖注入框架初始化
        // 如 Hilt、Koin 等
    }
}
