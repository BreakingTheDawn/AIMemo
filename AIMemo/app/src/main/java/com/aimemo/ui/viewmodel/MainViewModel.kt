package com.aimemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.data.repository.ScheduleRepository
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
     * 更新日程
     */
    fun updateSchedule(
        event: String,
        time: String,
        location: String,
        priority: String
    ) {
        val schedule = _selectedSchedule.value ?: return

        viewModelScope.launch {
            val updatedSchedule = schedule.copy(
                event = event,
                time = time,
                location = location,
                priority = priority
            )
            repository.updateSchedule(updatedSchedule)
            _showEditDialog.value = false
            _selectedSchedule.value = null
            _successMessage.value = "日程已更新"
        }
    }

    /**
     * 删除日程
     */
    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
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
