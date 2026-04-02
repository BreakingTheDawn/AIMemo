package com.aimemo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 快捷短语选择组件
 * 提供常用的日程输入短语，方便用户快速测试和使用
 * 
 * @param onPhraseSelected 短语选择回调
 * @param modifier 修饰符
 */
@Composable
fun QuickPhraseChips(
    onPhraseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 预定义的快捷短语列表
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
                label = { Text(phrase) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            )
        }
    }
}
