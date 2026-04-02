package com.aimemo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.ui.components.ApiKeyInputDialog
import com.aimemo.ui.components.QuickPhraseChips
import com.aimemo.ui.components.ScheduleCard
import com.aimemo.ui.components.ScheduleDetailDialog
import com.aimemo.ui.components.ScheduleEditDialog
import com.aimemo.ui.components.ShimmerScheduleCard
import com.aimemo.ui.components.SwipeableScheduleCard
import com.aimemo.ui.components.VoiceInputButton
import com.aimemo.ui.viewmodel.MainViewModel
import com.aimemo.util.CalendarUtils

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
    val themeMode by viewModel.themeMode.collectAsState()

    // 主题切换对话框状态
    var showThemeDialog by remember { mutableStateOf(false) }

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
                    // 主题切换按钮
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = if (themeMode == "dark") Icons.Default.DarkMode else if (themeMode == "light") Icons.Default.LightMode else Icons.Default.BrightnessMedium,
                            contentDescription = "切换主题",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
            AnimatedVisibility(
                visible = !isLoading,
                enter = slideInVertically(
                    animationSpec = tween(300)
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = slideOutVertically(
                    animationSpec = tween(300)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                InputSection(
                    inputText = inputText,
                    isLoading = isLoading,
                    onInputChange = { viewModel.updateInputText(it) },
                    onParseClick = { viewModel.parseText() },
                    onClearClick = { viewModel.clearInput() },
                    onVoiceText = { text ->
                        viewModel.updateInputText(text)
                    },
                    onVoiceError = { error ->
                        viewModel.setErrorMessage(error)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 日程列表区域
            ScheduleListSection(
                schedules = schedules,
                isLoading = isLoading,
                onScheduleClick = { viewModel.showScheduleDetail(it) },
                onScheduleDismiss = { viewModel.deleteSchedule(it) },
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
            onDelete = { viewModel.deleteSchedule(selectedSchedule!!) },
            onAddToCalendar = {
                // 添加到系统日历
                val calendarIntent = CalendarUtils.createCalendarIntent(selectedSchedule!!)
                context.startActivity(Intent.createChooser(calendarIntent, "添加到日历"))
            },
            onShare = {
                // 分享日程
                val shareIntent = CalendarUtils.createShareIntent(selectedSchedule!!)
                context.startActivity(Intent.createChooser(shareIntent, "分享日程"))
            }
        )
    }

    // 日程编辑对话框
    if (showEditDialog && selectedSchedule != null) {
        ScheduleEditDialog(
            schedule = selectedSchedule!!,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { event, time, location, priority, reminderEnabled, reminderMinutesBefore ->
                viewModel.updateSchedule(event, time, location, priority, reminderEnabled, reminderMinutesBefore)
            }
        )
    }

    // 主题切换对话框
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = themeMode,
            onDismiss = { showThemeDialog = false },
            onModeSelected = { mode ->
                viewModel.setThemeMode(mode)
                showThemeDialog = false
            }
        )
    }
}

/**
 * 主题选择对话框
 */
@Composable
private fun ThemeSelectionDialog(
    currentMode: String,
    onDismiss: () -> Unit,
    onModeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择主题") },
        text = {
            Column {
                ThemeOption(
                    text = "浅色模式",
                    icon = Icons.Default.LightMode,
                    selected = currentMode == "light",
                    onClick = { onModeSelected("light") }
                )
                ThemeOption(
                    text = "深色模式",
                    icon = Icons.Default.DarkMode,
                    selected = currentMode == "dark",
                    onClick = { onModeSelected("dark") }
                )
                ThemeOption(
                    text = "跟随系统",
                    icon = Icons.Default.BrightnessMedium,
                    selected = currentMode == "system",
                    onClick = { onModeSelected("system") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 主题选项组件
 */
@Composable
private fun ThemeOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (selected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
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
    onVoiceText: (String) -> Unit,
    onVoiceError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 文本输入框
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("输入日程文本") },
            placeholder = { 
                Text(
                    text = "例如：下周二早上10点在南山区科技园有个产品会",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
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

        // 快捷短语选择
        QuickPhraseChips(
            onPhraseSelected = { phrase ->
                onInputChange(phrase)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 操作按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 语音输入按钮
            VoiceInputButton(
                onTextRecognized = { text ->
                    onVoiceText(text)
                },
                onError = { error ->
                    onVoiceError(error)
                }
            )

            // 右侧按钮组
            Row(
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
                    enabled = inputText.isNotEmpty() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isLoading) "解析中..." else "AI 智能解析")
                }
            }
        }
    }
}

/**
 * 日程列表区域组件
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleListSection(
    schedules: List<ScheduleEntity>,
    isLoading: Boolean,
    onScheduleClick: (ScheduleEntity) -> Unit,
    onScheduleDismiss: (ScheduleEntity) -> Unit,
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
        when {
            isLoading -> {
                // 显示Shimmer加载效果
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        ShimmerScheduleCard()
                    }
                }
            }
            schedules.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // 使用SwipeableScheduleCard实现滑动删除
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = schedules,
                        key = { it.id },
                        contentType = { "schedule" } // 性能优化：标识列表项类型
                    ) { schedule ->
                        SwipeableScheduleCard(
                            schedule = schedule,
                            onDismiss = { onScheduleDismiss(schedule) },
                            onClick = { onScheduleClick(schedule) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 空状态组件
 * 当日程列表为空时显示，提供友好的用户引导
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
            // 使用更大的图标增强视觉效果
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 主标题
            Text(
                text = "暂无日程",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 引导文字
            Text(
                text = "粘贴会议记录或行程\n让我为你整理",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
