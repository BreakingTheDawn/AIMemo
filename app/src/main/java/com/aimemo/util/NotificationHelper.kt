package com.aimemo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aimemo.MainActivity
import com.aimemo.R
import com.aimemo.data.model.ScheduleEntity

/**
 * 通知帮助类
 * 负责创建通知渠道和发送日程提醒通知
 */
object NotificationHelper {

    // 通知渠道ID
    private const val CHANNEL_ID = "schedule_reminder_channel"
    private const val CHANNEL_NAME = "日程提醒"
    private const val CHANNEL_DESCRIPTION = "日程到期提醒通知"

    // 高优先级通知渠道
    private const val CHANNEL_ID_HIGH = "schedule_reminder_channel_high"
    private const val CHANNEL_NAME_HIGH = "紧急日程提醒"
    private const val CHANNEL_DESCRIPTION_HIGH = "高优先级日程紧急提醒"

    // 通知ID前缀（用于区分不同日程的通知）
    private const val NOTIFICATION_ID_PREFIX = 1000

    /**
     * 创建通知渠道
     * Android 8.0+ 需要创建通知渠道
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 普通通知渠道
            val normalChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(normalChannel)

            // 高优先级通知渠道（更强烈的提醒）
            val highChannel = NotificationChannel(CHANNEL_ID_HIGH, CHANNEL_NAME_HIGH, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANNEL_DESCRIPTION_HIGH
                enableVibration(true)
                enableLights(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000) // 更长的震动模式
                setSound(
                    android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM),
                    null
                )
            }
            notificationManager.createNotificationChannel(highChannel)
        }
    }

    /**
     * 发送日程提醒通知
     * 根据优先级使用不同的通知渠道和提醒方式
     *
     * @param context 上下文
     * @param schedule 日程实体
     */
    fun showReminderNotification(context: Context, schedule: ScheduleEntity) {
        // 判断是否为高优先级
        val isHighPriority = schedule.priority == "高"

        // 根据优先级选择通知渠道
        val channelId = if (isHighPriority) CHANNEL_ID_HIGH else CHANNEL_ID

        // 创建点击通知时打开应用的Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("schedule_id", schedule.id)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            schedule.id.toInt(),
            intent,
            pendingIntentFlags
        )

        // 根据优先级设置不同的震动模式
        val vibrationPattern = if (isHighPriority) {
            longArrayOf(0, 1000, 500, 1000, 500, 1000) // 高优先级：更长的震动
        } else {
            longArrayOf(0, 500, 200, 500) // 普通优先级：标准震动
        }

        // 构建通知标题（高优先级添加紧急标识）
        val notificationTitle = if (isHighPriority) {
            "🔴 紧急提醒：${schedule.event}"
        } else {
            "日程提醒：${schedule.event}"
        }

        // 构建通知
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(buildNotificationContent(schedule))
            .setPriority(if (isHighPriority) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern)
            .apply {
                // 高优先级通知设置为持续提醒
                if (isHighPriority) {
                    setOnlyAlertOnce(false)
                    setTimeoutAfter(60000) // 1分钟后自动取消
                }
            }
            .build()

        // 发送通知
        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(getNotificationId(schedule.id), notification)
        } catch (e: SecurityException) {
            // Android 13+ 需要通知权限
            e.printStackTrace()
        }
    }

    /**
     * 取消日程提醒通知
     */
    fun cancelNotification(context: Context, scheduleId: Long) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(getNotificationId(scheduleId))
    }

    /**
     * 构建通知内容
     */
    private fun buildNotificationContent(schedule: ScheduleEntity): String {
        val sb = StringBuilder()

        // 添加时间信息
        if (schedule.time.isNotEmpty()) {
            sb.append("时间：${schedule.time}")
        }

        // 添加地点信息
        if (schedule.location.isNotEmpty()) {
            if (sb.isNotEmpty()) sb.append(" | ")
            sb.append("地点：${schedule.location}")
        }

        return sb.toString()
    }

    /**
     * 获取通知ID
     */
    private fun getNotificationId(scheduleId: Long): Int {
        return NOTIFICATION_ID_PREFIX + scheduleId.toInt()
    }
}
