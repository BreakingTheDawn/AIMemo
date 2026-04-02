package com.aimemo.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 优先级计算工具类
 * 根据时间紧迫度和语义紧急程度综合评估日程优先级
 */
object PriorityCalculator {

    // 时间格式
    private const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    private const val DATE_FORMAT = "yyyy-MM-dd"

    // 优先级常量
    const val PRIORITY_HIGH = "高"
    const val PRIORITY_MEDIUM = "中"
    const val PRIORITY_LOW = "低"

    // 时间紧迫度阈值（毫秒）
    private const val URGENT_THRESHOLD = 24 * 60 * 60 * 1000L      // 24小时
    private const val MODERATE_THRESHOLD = 3 * 24 * 60 * 60 * 1000L // 3天

    // 高紧急关键词
    private val highUrgencyKeywords = listOf(
        "紧急", "立即", "马上", "必须", "尽快", "抓紧", "赶快", "火速", "加急", "急"
    )

    // 高重要关键词
    private val highImportanceKeywords = listOf(
        "重要", "关键", "务必", "一定要", "千万", "核心", "重大"
    )

    // 低紧急关键词
    private val lowUrgencyKeywords = listOf(
        "有空", "方便时", "不急", "慢慢", "随意", "闲时", "得空", "不着急"
    )

    /**
     * 计算日程优先级
     * 综合时间紧迫度和语义紧急程度
     *
     * @param timeStr 时间字符串，格式为 yyyy-MM-dd HH:mm
     * @param originalText 原始输入文本
     * @param aiPriority AI返回的优先级（可选参考）
     * @return 计算后的优先级（高/中/低）
     */
    fun calculatePriority(
        timeStr: String,
        originalText: String,
        aiPriority: String = PRIORITY_MEDIUM
    ): String {
        // 评估时间紧迫度
        val timeUrgency = evaluateTimeUrgency(timeStr)

        // 评估语义紧急程度
        val semanticUrgency = evaluateSemanticUrgency(originalText)

        // 综合判定
        return determineFinalPriority(timeUrgency, semanticUrgency, aiPriority)
    }

    /**
     * 评估时间紧迫度
     *
     * @param timeStr 时间字符串
     * @return 时间紧迫度等级（HIGH/MEDIUM/LOW/UNKNOWN）
     */
    private fun evaluateTimeUrgency(timeStr: String): UrgencyLevel {
        if (timeStr.isEmpty()) {
            return UrgencyLevel.UNKNOWN
        }

        val scheduleTime = parseTimeToMillis(timeStr)
        if (scheduleTime == null) {
            return UrgencyLevel.UNKNOWN
        }

        val currentTime = System.currentTimeMillis()
        val timeDiff = scheduleTime - currentTime

        // 时间已过
        if (timeDiff < 0) {
            return UrgencyLevel.UNKNOWN
        }

        return when {
            timeDiff <= URGENT_THRESHOLD -> UrgencyLevel.HIGH      // 24小时内
            timeDiff <= MODERATE_THRESHOLD -> UrgencyLevel.MEDIUM  // 1-3天
            else -> UrgencyLevel.LOW                               // 3天以上
        }
    }

    /**
     * 评估语义紧急程度
     *
     * @param text 原始文本
     * @return 语义紧急程度等级
     */
    private fun evaluateSemanticUrgency(text: String): UrgencyLevel {
        val lowerText = text.lowercase(Locale.getDefault())

        // 检查高紧急关键词
        if (highUrgencyKeywords.any { lowerText.contains(it) }) {
            return UrgencyLevel.HIGH
        }

        // 检查高重要关键词
        if (highImportanceKeywords.any { lowerText.contains(it) }) {
            return UrgencyLevel.HIGH
        }

        // 检查低紧急关键词
        if (lowUrgencyKeywords.any { lowerText.contains(it) }) {
            return UrgencyLevel.LOW
        }

        return UrgencyLevel.MEDIUM
    }

    /**
     * 综合判定最终优先级
     *
     * @param timeUrgency 时间紧迫度
     * @param semanticUrgency 语义紧急程度
     * @param aiPriority AI返回的优先级
     * @return 最终优先级
     */
    private fun determineFinalPriority(
        timeUrgency: UrgencyLevel,
        semanticUrgency: UrgencyLevel,
        aiPriority: String
    ): String {
        // 规则1：时间紧迫度高 或 语义紧急高 → 高优先级
        if (timeUrgency == UrgencyLevel.HIGH || semanticUrgency == UrgencyLevel.HIGH) {
            return PRIORITY_HIGH
        }

        // 规则2：时间紧迫度低 且 语义紧急低 → 低优先级
        if (timeUrgency == UrgencyLevel.LOW && semanticUrgency == UrgencyLevel.LOW) {
            return PRIORITY_LOW
        }

        // 规则3：时间紧迫度未知 且 语义紧急低 → 低优先级
        if (timeUrgency == UrgencyLevel.UNKNOWN && semanticUrgency == UrgencyLevel.LOW) {
            return PRIORITY_LOW
        }

        // 规则4：时间紧迫度低/未知 且 语义紧急中 → 参考AI判断
        if ((timeUrgency == UrgencyLevel.LOW || timeUrgency == UrgencyLevel.UNKNOWN) 
            && semanticUrgency == UrgencyLevel.MEDIUM) {
            return aiPriority
        }

        // 其他情况 → 中优先级
        return PRIORITY_MEDIUM
    }

    /**
     * 解析时间字符串为毫秒时间戳
     *
     * @param timeStr 时间字符串
     * @return 毫秒时间戳，解析失败返回null
     */
    private fun parseTimeToMillis(timeStr: String): Long? {
        if (timeStr.isEmpty()) return null

        return try {
            val format = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val date = format.parse(timeStr)
            date?.time
        } catch (e: Exception) {
            // 尝试只解析日期
            try {
                val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                val date = dateFormat.parse(timeStr.substring(0, 10))
                date?.time
            } catch (e2: Exception) {
                null
            }
        }
    }

    /**
     * 获取时间紧迫度描述
     *
     * @param timeStr 时间字符串
     * @return 紧迫度描述文本
     */
    fun getUrgencyDescription(timeStr: String): String {
        if (timeStr.isEmpty()) {
            return "无明确时间"
        }

        val scheduleTime = parseTimeToMillis(timeStr)
        if (scheduleTime == null) {
            return "时间格式错误"
        }

        val currentTime = System.currentTimeMillis()
        val timeDiff = scheduleTime - currentTime

        if (timeDiff < 0) {
            return "已过期"
        }

        val hours = TimeUnit.MILLISECONDS.toHours(timeDiff)
        val days = TimeUnit.MILLISECONDS.toDays(timeDiff)

        return when {
            hours < 1 -> "即将到期"
            hours < 24 -> "${hours}小时后"
            days <= 3 -> "${days}天后"
            else -> "${days}天后"
        }
    }

    /**
     * 紧急程度等级枚举
     */
    private enum class UrgencyLevel {
        HIGH,    // 高
        MEDIUM,  // 中
        LOW,     // 低
        UNKNOWN  // 未知（无法判断）
    }
}
