package com.aimemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.util.NotificationHelper

/**
 * 日程提醒广播接收器
 * 接收AlarmManager的闹钟广播，发送提醒通知
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 从Intent中获取日程信息
        val scheduleId = intent.getLongExtra("schedule_id", 0)
        val event = intent.getStringExtra("event") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val priority = intent.getStringExtra("priority") ?: "中"

        // 创建日程实体（用于发送通知）
        val schedule = ScheduleEntity(
            id = scheduleId,
            originalText = "",
            event = event,
            time = time,
            location = location,
            priority = priority,
            reminderEnabled = true
        )

        // 发送提醒通知
        NotificationHelper.showReminderNotification(context, schedule)
    }
}
