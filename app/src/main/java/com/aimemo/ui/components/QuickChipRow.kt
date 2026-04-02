package com.aimemo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 快捷短语行组件
 * 提供常用的日程输入短语，方便用户快速测试和使用
 * 使用 Material 3 SuggestionChip 展示，支持横向滚动
 *
 * 设计要点：
 * - 图标使用微弱的主题色填充（alpha=0.6），不抢视觉焦点
 * - 文字颜色使用 onSurfaceVariant，保持适度对比度
 * - LazyRow 实现高效横向滚动
 *
 * @param onPhraseSelected 短语被选中时的回调，返回选中的短语文本
 * @param modifier 修饰符，用于自定义布局和样式
 */
@Composable
fun QuickChipRow(
    onPhraseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 预定义的快捷短语列表，覆盖常见日程场景
    val phrases = listOf(
        "明天下午三点开会",
        "周五去上海出差",
        "下周一上午10点面试",
        "今晚8点团队聚餐",
        "后天下午2点客户拜访",
        "周三早上9点项目评审"
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(phrases) { phrase ->
            SuggestionChip(
                onClick = { onPhraseSelected(phrase) },
                label = {
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "快捷短语",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}
