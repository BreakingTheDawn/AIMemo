package com.aimemo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aimemo.data.model.ScheduleEntity

/**
 * API密钥输入对话框
 * 用于让用户输入GLM API密钥
 */
@Composable
fun ApiKeyInputDialog(
    initialKey: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var apiKey by remember { mutableStateOf(initialKey) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "输入API密钥")
        },
        text = {
            Column {
                Text(
                    text = "请输入您的GLM API密钥以启用AI解析功能",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { 
                        apiKey = it
                        showError = false
                    },
                    label = { Text("API密钥") },
                    placeholder = { Text("请输入GLM API密钥") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("API密钥不能为空") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (apiKey.isNotBlank()) {
                        onConfirm(apiKey)
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 日程详情对话框
 * 显示日程的完整信息
 */
@Composable
fun ScheduleDetailDialog(
    schedule: ScheduleEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "日程详情")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 事件
                DetailItem(label = "事件", value = schedule.event)
                Spacer(modifier = Modifier.height(12.dp))
                
                // 时间
                DetailItem(label = "时间", value = schedule.time.ifEmpty { "未指定" })
                Spacer(modifier = Modifier.height(12.dp))
                
                // 地点
                DetailItem(label = "地点", value = schedule.location.ifEmpty { "未指定" })
                Spacer(modifier = Modifier.height(12.dp))
                
                // 优先级
                DetailItem(label = "优先级", value = schedule.priority)
                Spacer(modifier = Modifier.height(12.dp))
                
                // 原始文本
                DetailItem(label = "原始文本", value = schedule.originalText)
            }
        },
        confirmButton = {
            Row {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onEdit) {
                    Text("编辑")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

/**
 * 详情项组件
 */
@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 日程编辑对话框
 * 用于编辑日程信息
 */
@Composable
fun ScheduleEditDialog(
    schedule: ScheduleEntity,
    onDismiss: () -> Unit,
    onSave: (event: String, time: String, location: String, priority: String) -> Unit
) {
    var event by remember { mutableStateOf(schedule.event) }
    var time by remember { mutableStateOf(schedule.time) }
    var location by remember { mutableStateOf(schedule.location) }
    var priority by remember { mutableStateOf(schedule.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "编辑日程")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 事件
                OutlinedTextField(
                    value = event,
                    onValueChange = { event = it },
                    label = { Text("事件") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // 时间
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("时间") },
                    placeholder = { Text("yyyy-MM-dd HH:mm") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // 地点
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("地点") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // 优先级选择
                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriorityOption(
                        text = "高",
                        selected = priority == "高",
                        onClick = { priority = "高" }
                    )
                    PriorityOption(
                        text = "中",
                        selected = priority == "中",
                        onClick = { priority = "中" }
                    )
                    PriorityOption(
                        text = "低",
                        selected = priority == "低",
                        onClick = { priority = "低" }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(event, time, location, priority) },
                enabled = event.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 优先级选项按钮
 */
@Composable
private fun PriorityOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}
