package com.aimemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 日程事件数据实体类
 * 对应Room数据库中的日程表结构
 * 
 * 字段说明：
 * - id: 唯一主键，自增生成
 * - originalText: 用户输入的原始文本
 * - event: 提取的事件名称
 * - time: 解析后的时间（yyyy-MM-dd HH:mm格式）
 * - location: 解析后的地点
 * - priority: 优先级（高/中/低）
 * - createdAt: 创建时间
 * - updatedAt: 更新时间
 */
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val originalText: String,
    
    val event: String,
    
    val time: String,
    
    val location: String,
    
    val priority: String,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis()
)
