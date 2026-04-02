package com.aimemo.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.aimemo.data.model.ScheduleEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 日历工具类
 * 提供系统日历Intent创建功能
 */
object CalendarUtils {

    private const val DEFAULT_EVENT_DURATION = 60 * 60 * 1000L // 默认事件时长1小时

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    /**
     * 创建添加日程到系统日历的Intent
     * @param context 上下文
     * @param schedule 日程实体
     * @return Intent对象
     */
    fun createCalendarIntent(context: Context, schedule: ScheduleEntity): Intent {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, schedule.event)
            putExtra(CalendarContract.Events.DESCRIPTION, "原始输入: ${schedule.originalText}")
            putExtra(CalendarContract.Events.EVENT_LOCATION, schedule.location)

            // 解析时间
            val startTime = parseTime(schedule.time)
            if (startTime > 0) {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + DEFAULT_EVENT_DURATION)
            }
        }
        return intent
    }

    /**
     * 解析时间字符串为时间戳
     * @param timeStr 时间字符串（yyyy-MM-dd HH:mm格式）
     * @return 时间戳，解析失败返回0
     */
    private fun parseTime(timeStr: String): Long {
        if (timeStr.isBlank()) return 0

        return try {
            val date = dateFormat.parse(timeStr)
            date?.time ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 创建分享日程的Intent
     * @param schedule 日程实体
     * @return Intent对象
     */
    fun createShareIntent(schedule: ScheduleEntity): Intent {
        val shareText = buildShareText(schedule)
        
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, schedule.event)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }

    /**
     * 构建分享文本
     */
    private fun buildShareText(schedule: ScheduleEntity): String {
        return buildString {
            append("📅 ${schedule.event}\n")
            if (schedule.time.isNotEmpty()) {
                append("⏰ ${schedule.time}\n")
            }
            if (schedule.location.isNotEmpty()) {
                append("📍 ${schedule.location}\n")
            }
            append("⚡ 优先级: ${schedule.priority}\n")
            append("\n—— 来自 AI Memo 智能日程记事本")
        }
    }
}
