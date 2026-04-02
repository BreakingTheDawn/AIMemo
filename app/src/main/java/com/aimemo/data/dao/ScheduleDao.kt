package com.aimemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aimemo.data.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * 日程数据访问对象（DAO）
 * 定义日程表的CRUD操作接口
 * 
 * 使用Flow实现响应式数据更新
 */
@Dao
interface ScheduleDao {

    /**
     * 插入新的日程记录
     * @param schedule 日程实体
     * @return 插入后的行ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity): Long

    /**
     * 更新现有日程记录
     * @param schedule 日程实体
     */
    @Update
    suspend fun update(schedule: ScheduleEntity)

    /**
     * 删除指定日程记录
     * @param schedule 日程实体
     */
    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    /**
     * 根据ID删除日程记录
     * @param id 日程ID
     */
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * 批量删除多个日程记录
     * @param ids 日程ID列表
     */
    @Query("DELETE FROM schedules WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    /**
     * 根据ID查询单条日程记录
     * @param id 日程ID
     * @return 日程实体（可null）
     */
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getById(id: Long): ScheduleEntity?

    /**
     * 查询所有日程记录（按创建时间倒序）
     * @return Flow<List<ScheduleEntity>> 响应式数据流
     */
    @Query("SELECT * FROM schedules ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ScheduleEntity>>

    /**
     * 根据关键字搜索日程
     * @param keyword 搜索关键字
     * @return 符合条件的日程列表
     */
    @Query("SELECT * FROM schedules WHERE event LIKE '%' || :keyword || '%' OR originalText LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    suspend fun search(keyword: String): List<ScheduleEntity>

    /**
     * 删除所有日程记录
     */
    @Query("DELETE FROM schedules")
    suspend fun deleteAll()
}
