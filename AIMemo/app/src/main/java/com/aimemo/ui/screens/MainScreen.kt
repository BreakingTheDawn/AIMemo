package com.aimemo.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.ui.components.ApiKeyInputDialog
import com.aimemo.ui.components.ScheduleCard
import com.aimemo.ui.components.ScheduleDetailDialog
import com.aimemo.ui.components.ScheduleEditDialog
import com.aimemo.ui.viewmodel.MainViewModel

/**
 * 主屏幕
 * 应用程序的主要界面，包含输入区和日程列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // 收集状态
    val schedules by viewModel.schedules.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val showApiKeyDialog by viewModel.showApiKeyDialog.collectAsState()
    val showDetailDialog by viewModel.showDetailDialog.collectAsState()
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val selectedSchedule by viewModel.selectedSchedule.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()

    // 显示错误消息
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage()
        }
    }

    // 显示成功消息
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

    // 主界面结构
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Memo",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // API密钥设置按钮
                    IconButton(onClick = { viewModel.showApiKeyInputDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "设置API密钥",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 输入区域
            InputSection(
                inputText = inputText,
                isLoading = isLoading,
                onInputChange = { viewModel.updateInputText(it) },
                onParseClick = { viewModel.parseText() },
                onClearClick = { viewModel.clearInput() },
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日程列表区域
            ScheduleListSection(
                schedules = schedules,
                onScheduleClick = { viewModel.showScheduleDetail(it) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // API密钥输入对话框
    if (showApiKeyDialog) {
        ApiKeyInputDialog(
            initialKey = apiKey,
            onDismiss = { viewModel.hideApiKeyDialog() },
            onConfirm = { key ->
                viewModel.updateApiKey(key)
                viewModel.hideApiKeyDialog()
            }
        )
    }

    // 日程详情对话框
    if (showDetailDialog && selectedSchedule != null) {
        ScheduleDetailDialog(
            schedule = selectedSchedule!!,
            onDismiss = { viewModel.hideDetailDialog() },
            onEdit = { viewModel.showEditDialog(selectedSchedule!!) },
            onDelete = { viewModel.deleteSchedule(selectedSchedule!!) }
        )
    }

    // 日程编辑对话框
    if (showEditDialog && selectedSchedule != null) {
        ScheduleEditDialog(
            schedule = selectedSchedule!!,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { event, time, location, priority ->
                viewModel.updateSchedule(event, time, location, priority)
            }
        )
    }
}

/**
 * 输入区域组件
 */
@Composable
private fun InputSection(
    inputText: String,
    isLoading: Boolean,
    onInputChange: (String) -> Unit,
    onParseClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 文本输入框
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("输入日程文本") },
            placeholder = { Text("例如：下周二早上10点在南山区科技园有个产品会") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            trailingIcon = {
                if (inputText.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清空"
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // 清空按钮
            OutlinedButton(
                onClick = onClearClick,
                enabled = inputText.isNotEmpty() && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("清空")
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 解析按钮
            Button(
                onClick = onParseClick,
                enabled = inputText.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLoading) "解析中..." else "解析")
            }
        }
    }
}

/**
 * 日程列表区域组件
 */
@Composable
private fun ScheduleListSection(
    schedules: List<ScheduleEntity>,
    onScheduleClick: (ScheduleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "日程列表",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "共 ${schedules.size} 条",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 列表内容
        if (schedules.isEmpty()) {
            EmptyState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = schedules,
                    key = { it.id }
                ) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onClick = { onScheduleClick(schedule) }
                    )
                }
            }
        }
    }
}

/**
 * 空状态组件
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无日程",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "输入文本并点击解析按钮\nAI将自动提取日程信息",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
