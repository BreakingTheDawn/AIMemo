package com.aimemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aimemo.data.local.ThemePreferences
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.data.repository.ScheduleRepository
import com.aimemo.util.NotificationHelper
import com.aimemo.util.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 主界面ViewModel
 * 管理日程数据和UI状态
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)
    private val context = application.applicationContext

    // 日程列表（响应式数据流）
    val schedules: StateFlow<List<ScheduleEntity>> = repository.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 输入文本状态
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    // API密钥状态
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 成功消息
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // 是否显示API密钥输入对话框
    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()

    // 当前选中的日程（用于编辑）
    private val _selectedSchedule = MutableStateFlow<ScheduleEntity?>(null)
    val selectedSchedule: StateFlow<ScheduleEntity?> = _selectedSchedule.asStateFlow()

    // 是否显示编辑对话框
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    // 是否显示详情对话框
    private val _showDetailDialog = MutableStateFlow(false)
    val showDetailDialog: StateFlow<Boolean> = _showDetailDialog.asStateFlow()

    // 主题模式状态
    private val _themeMode = MutableStateFlow("system")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    /**
     * 初始化
     */
    init {
        // 初始化通知渠道
        NotificationHelper.createNotificationChannel(context)
        
        // 加载主题偏好
        viewModelScope.launch {
            ThemePreferences.getThemeMode(context).collect { mode ->
                _themeMode.value = mode
            }
        }
    }

    /**
     * 设置主题模式
     */
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            ThemePreferences.setThemeMode(context, mode)
            _themeMode.value = mode
        }
    }

    /**
     * 更新输入文本
     */
    fun updateInputText(text: String) {
        _inputText.value = text
    }

    /**
     * 更新API密钥
     */
    fun updateApiKey(key: String) {
        _apiKey.value = key
    }

    /**
     * 显示API密钥输入对话框
     */
    fun showApiKeyInputDialog() {
        _showApiKeyDialog.value = true
    }

    /**
     * 隐藏API密钥输入对话框
     */
    fun hideApiKeyDialog() {
        _showApiKeyDialog.value = false
    }

    /**
     * 解析文本
     */
    fun parseText() {
        val text = _inputText.value.trim()
        val key = _apiKey.value.trim()

        // 验证输入
        if (text.isEmpty()) {
            _errorMessage.value = "请输入要解析的文本"
            return
        }

        if (key.isEmpty()) {
            _showApiKeyDialog.value = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.parseAndSave(text, key)

            _isLoading.value = false

            result.fold(
                onSuccess = { schedule ->
                    _inputText.value = ""
                    _successMessage.value = "日程已添加：${schedule.event}"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "解析失败，请重试"
                }
            )
        }
    }

    /**
     * 显示日程详情
     */
    fun showScheduleDetail(schedule: ScheduleEntity) {
        _selectedSchedule.value = schedule
        _showDetailDialog.value = true
    }

    /**
     * 隐藏详情对话框
     */
    fun hideDetailDialog() {
        _showDetailDialog.value = false
        _selectedSchedule.value = null
    }

    /**
     * 显示编辑对话框
     */
    fun showEditDialog(schedule: ScheduleEntity) {
        _selectedSchedule.value = schedule
        _showEditDialog.value = true
        _showDetailDialog.value = false
    }

    /**
     * 隐藏编辑对话框
     */
    fun hideEditDialog() {
        _showEditDialog.value = false
    }

    /**
     * 更新日程（包含提醒设置）
     */
    fun updateSchedule(
        event: String,
        time: String,
        location: String,
        priority: String,
        reminderEnabled: Boolean = false,
        reminderMinutesBefore: Int = 15
    ) {
        val schedule = _selectedSchedule.value ?: return

        viewModelScope.launch {
            val updatedSchedule = schedule.copy(
                event = event,
                time = time,
                location = location,
                priority = priority,
                reminderEnabled = reminderEnabled,
                reminderMinutesBefore = reminderMinutesBefore
            )
            repository.updateSchedule(updatedSchedule)
            
            // 重新设置提醒
            if (reminderEnabled) {
                val success = ReminderManager.rescheduleReminder(context, updatedSchedule)
                if (!success) {
                    _errorMessage.value = "提醒时间已过，无法设置提醒"
                }
            } else {
                ReminderManager.cancelReminder(context, updatedSchedule)
            }
            
            _showEditDialog.value = false
            _selectedSchedule.value = null
            _successMessage.value = "日程已更新"
        }
    }

    /**
     * 设置日程提醒
     */
    fun setReminder(schedule: ScheduleEntity, minutesBefore: Int) {
        viewModelScope.launch {
            val updatedSchedule = schedule.copy(
                reminderEnabled = true,
                reminderMinutesBefore = minutesBefore
            )
            repository.updateSchedule(updatedSchedule)
            
            val success = ReminderManager.setReminder(context, updatedSchedule)
            if (success) {
                _successMessage.value = "已设置提醒，提前${minutesBefore}分钟通知"
            } else {
                _errorMessage.value = "提醒时间已过，无法设置提醒"
            }
        }
    }

    /**
     * 取消日程提醒
     */
    fun cancelReminder(schedule: ScheduleEntity) {
        viewModelScope.launch {
            val updatedSchedule = schedule.copy(reminderEnabled = false)
            repository.updateSchedule(updatedSchedule)
            ReminderManager.cancelReminder(context, schedule)
            _successMessage.value = "已取消提醒"
        }
    }

    /**
     * 删除日程
     */
    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            // 取消提醒
            ReminderManager.cancelReminder(context, schedule)
            
            repository.deleteSchedule(schedule)
            _showDetailDialog.value = false
            _showEditDialog.value = false
            _selectedSchedule.value = null
            _successMessage.value = "日程已删除"
        }
    }

    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * 设置错误消息
     */
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    /**
     * 清除成功消息
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * 清空输入
     */
    fun clearInput() {
        _inputText.value = ""
    }
}
