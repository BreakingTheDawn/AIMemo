package com.aimemo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 日程输入卡片组件（NoteInputCard）
 *
 * AI Memo 的核心输入区域，将文本输入、快捷短语、操作按钮整合在一张 ElevatedCard 中。
 * 符合 Material 3 设计规范：24dp 大圆角、透明 TextField、层次分明的按钮组。
 *
 * 组件结构：
 * ┌─────────────────────────────┐
 * │  ElevatedCard (24dp 圆角)    │
 * │  ┌───────────────────────┐  │
 * │  │ TextField (透明样式)   │  │
 * │  │              [清空✕]  │  │
 * │  └───────────────────────┘  │
 * └─────────────────────────────┘
 * ▓▓▓ LinearProgressIndicator (加载时显示)
 * [💡快捷短语1] [💡快捷短语2] ...
 * [🎤语音] [清空] [✨AI智能解析(占满)]
 *
 * @param inputText 当前输入框中的文本内容
 * @param isLoading 是否正在加载（AI 解析进行中）
 * @param onInputChange 输入文本变更回调
 * @param onParseClick AI 智能解析按钮点击回调
 * @param onClearClick 清空按钮点击回调
 * @param onVoiceText 语音识别成功后的文本回调
 * @param onVoiceError 语音识别发生错误时的回调
 * @param modifier 修饰符，用于自定义布局和样式
 */
@Composable
fun NoteInputCard(
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
        // ========== 输入框卡片区域（24dp 大圆角 ElevatedCard）==========
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    // TextField：隐藏背景色和指示线，完全融入卡片背景
                    TextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        placeholder = {
                            Text(
                                text = "例如：下周二早上10点在南山区科技园有个产品会",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = transparentTextFieldColors()
                    )
                }

                // 右下角半透明清空按钮（仅输入非空时显示）
                if (inputText.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = 40.dp)
                            .alpha(0.6f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清空输入",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ========== 加载进度条（仅在 isLoading 时显示）==========
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // ========== 快捷短语行（SuggestionChip 横向滚动）==========
        QuickChipRow(
            onPhraseSelected = { phrase ->
                onInputChange(phrase)
            },
            modifier = Modifier.padding(top = 16.dp)
        )

        // ========== 操作按钮行（主次分明）==========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 语音输入按钮（FilledTonalButton - 辅助操作）
            VoiceInputButtonCompact(
                onTextRecognized = onVoiceText,
                onError = onVoiceError
            )

            // 清空按钮（OutlinedButton - 次要操作）
            OutlinedButton(
                onClick = onClearClick,
                enabled = inputText.isNotEmpty() && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("清空")
            }

            // AI 智能解析按钮（Button - 主操作，占据剩余空间）
            Button(
                onClick = onParseClick,
                enabled = inputText.isNotEmpty() && !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLoading) "解析中..." else "AI 智能解析")
            }
        }
    }
}

/**
 * 透明的 TextField 配色方案
 *
 * 隐藏 TextField 的容器背景色、聚焦/未聚焦指示线，
 * 使其完美融入 ElevatedCard 的背景中，
 * 实现文档要求的"去除原本沉重的黑边"效果。
 */
@Composable
private fun transparentTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
}
