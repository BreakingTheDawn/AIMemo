package com.aimemo.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.receiver.ReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 日程提醒管理器
 * 使用AlarmManager实现精确提醒功能
 */
object ReminderManager {

    // 日期时间格式
    private const val TIME_FORMAT = "yyyy-MM-dd HH:mm"

    /**
     * 设置日程提醒
     * 
     * @param context 上下文
     * @param schedule 日程实体
     * @return 是否设置成功
     */
    fun setReminder(context: Context, schedule: ScheduleEntity): Boolean {
        // 解析日程时间
        val scheduleTime = parseScheduleTime(schedule.time)
        if (scheduleTime == null) {
            return false
        }

        // 计算提醒时间（日程时间 - 提前分钟数）
        val reminderTime = scheduleTime.timeInMillis - (schedule.reminderMinutesBefore * 60 * 1000L)

        // 如果提醒时间已过，不设置提醒
        if (reminderTime <= System.currentTimeMillis()) {
            return false
        }

        // 获取AlarmManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 创建提醒触发的Intent
        val intent = createReminderIntent(context, schedule)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 设置精确闹钟
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ 需要检查精确闹钟权限
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } else {
                    // 没有精确闹钟权限，使用非精确闹钟
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )
            }
            return true
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 取消日程提醒
     * 
     * @param context 上下文
     * @param schedule 日程实体
     */
    fun cancelReminder(context: Context, schedule: ScheduleEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createReminderIntent(context, schedule)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        
        // 同时取消通知
        NotificationHelper.cancelNotification(context, schedule.id)
    }

    /**
     * 重新设置提醒
     * 用于更新日程时重新计算提醒时间
     */
    fun rescheduleReminder(context: Context, schedule: ScheduleEntity): Boolean {
        // 先取消旧的提醒
        cancelReminder(context, schedule)
        
        // 如果启用了提醒，设置新的提醒
        if (schedule.reminderEnabled) {
            return setReminder(context, schedule)
        }
        return true
    }

    /**
     * 创建提醒触发的Intent
     */
    private fun createReminderIntent(context: Context, schedule: ScheduleEntity): Intent {
        return Intent(context, ReminderReceiver::class.java).apply {
            putExtra("schedule_id", schedule.id)
            putExtra("event", schedule.event)
            putExtra("time", schedule.time)
            putExtra("location", schedule.location)
            putExtra("priority", schedule.priority)
        }
    }

    /**
     * 解析日程时间字符串
     * 
     * @param timeStr 时间字符串，格式为 yyyy-MM-dd HH:mm
     * @return Calendar对象，解析失败返回null
     */
    private fun parseScheduleTime(timeStr: String): Calendar? {
        if (timeStr.isEmpty()) return null

        return try {
            val format = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val date = format.parse(timeStr)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 计算提醒时间
     * 
     * @param scheduleTime 日程时间（毫秒）
     * @param minutesBefore 提前分钟数
     * @return 提醒时间（毫秒）
     */
    fun calculateReminderTime(scheduleTime: Long, minutesBefore: Int): Long {
        return scheduleTime - (minutesBefore * 60 * 1000L)
    }
}
