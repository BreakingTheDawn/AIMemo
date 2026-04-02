package com.aimemo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aimemo.data.model.ScheduleEntity

/**
 * 可滑动删除的日程卡片
 * 支持左滑删除功能，增强用户交互体验
 * 
 * @param schedule 日程数据实体
 * @param onDismiss 滑动删除回调
 * @param onClick 点击事件回调
 * @param modifier 修饰符，用于自定义布局和样式
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableScheduleCard(
    schedule: ScheduleEntity,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 创建滑动状态，设置删除阈值和确认逻辑
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            // 当滑动到删除位置时触发删除
            if (dismissValue == DismissValue.DismissedToStart) {
                onDismiss()
                true
            } else {
                false
            }
        },
        // 滑动超过50%即触发删除
        positionalThreshold = { it * 0.5f }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        // 背景层：显示删除图标和红色背景
        background = {
            // 根据滑动状态动态改变背景颜色，使用主题色
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error // 使用主题错误色
                    else -> MaterialTheme.colorScheme.surfaceVariant // 使用主题表面变体色
                },
                label = "background_color"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        // 前景层：显示日程卡片内容
        dismissContent = {
            ScheduleCard(
                schedule = schedule,
                onClick = onClick
            )
        },
        // 只支持从右向左滑动删除
        directions = setOf(DismissDirection.EndToStart)
    )
}
