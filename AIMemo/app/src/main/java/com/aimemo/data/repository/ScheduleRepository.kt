package com.aimemo.data.repository

import android.content.Context
import android.util.Log
import com.aimemo.data.dao.ScheduleDao
import com.aimemo.data.local.AIMemoDatabase
import com.aimemo.data.model.AIParseResult
import com.aimemo.data.model.GLMRequest
import com.aimemo.data.model.Message
import com.aimemo.data.model.ScheduleEntity
import com.aimemo.data.remote.NetworkClient
import com.aimemo.util.PriorityCalculator
import com.aimemo.util.PromptConstants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日程数据仓库
 * 统一管理本地数据源和远程数据源
 * 实现单一数据入口原则
 */
class ScheduleRepository(context: Context) {

    private val scheduleDao: ScheduleDao = AIMemoDatabase.getInstance(context).scheduleDao()
    private val glmApiService = NetworkClient.glmApiService
    private val gson = Gson()

    companion object {
        private const val TAG = "ScheduleRepository"
    }

    /**
     * 获取所有日程记录（响应式）
     */
    fun getAllSchedules(): Flow<List<ScheduleEntity>> {
        return scheduleDao.getAll()
    }

    /**
     * 根据ID获取单条日程记录
     */
    suspend fun getScheduleById(id: Long): ScheduleEntity? {
        return scheduleDao.getById(id)
    }

    /**
     * 解析文本并保存日程
     * @param inputText 用户输入的原始文本
     * @param apiKey GLM API密钥
     * @return 解析结果，失败返回null
     */
    suspend fun parseAndSave(inputText: String, apiKey: String): Result<ScheduleEntity> {
        return try {
            // 调用GLM API解析文本
            val parseResult = parseTextWithAI(inputText, apiKey)
            
            if (parseResult == null) {
                return Result.failure(Exception("AI解析失败，请检查API密钥或网络连接"))
            }

            // 创建日程实体并保存
            // 使用本地优先级校验，综合时间紧迫度和语义紧急程度
            val finalPriority = PriorityCalculator.calculatePriority(
                timeStr = parseResult.Time,
                originalText = inputText,
                aiPriority = parseResult.Priority
            )

            val schedule = ScheduleEntity(
                originalText = inputText,
                event = parseResult.Event,
                time = parseResult.Time,
                location = parseResult.Location,
                priority = finalPriority
            )

            val insertedId = scheduleDao.insert(schedule)
            val savedSchedule = schedule.copy(id = insertedId)

            Result.success(savedSchedule)
        } catch (e: Exception) {
            Log.e(TAG, "解析保存失败", e)
            Result.failure(e)
        }
    }

    /**
     * 调用GLM API解析文本
     */
    private suspend fun parseTextWithAI(inputText: String, apiKey: String): AIParseResult? {
        return try {
            // 构建请求
            val request = GLMRequest(
                model = "glm-4",
                messages = listOf(
                    Message(
                        role = "system",
                        content = PromptConstants.SYSTEM_PROMPT
                    ),
                    Message(
                        role = "user",
                        content = String.format(PromptConstants.USER_PROMPT_TEMPLATE, inputText)
                    )
                ),
                temperature = 0.3
            )

            // 发送请求
            val response = glmApiService.parseText(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val content = responseBody.choices?.firstOrNull()?.message?.content
                
                if (content != null) {
                    parseAIResponse(content)
                } else {
                    Log.e(TAG, "AI返回内容为空")
                    null
                }
            } else {
                Log.e(TAG, "API请求失败: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "API调用异常", e)
            null
        }
    }

    /**
     * 解析AI返回的JSON内容
     */
    private fun parseAIResponse(content: String): AIParseResult? {
        return try {
            // 清理可能的markdown代码块标记
            var jsonContent = content.trim()
            if (jsonContent.startsWith("```json")) {
                jsonContent = jsonContent.removePrefix("```json").removeSuffix("```").trim()
            } else if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.removePrefix("```").removeSuffix("```").trim()
            }

            // 解析JSON
            val result = gson.fromJson(jsonContent, AIParseResult::class.java)

            // 验证字段
            if (validateParseResult(result)) {
                result
            } else {
                Log.e(TAG, "解析结果字段验证失败")
                null
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON解析失败: $content", e)
            null
        }
    }

    /**
     * 验证解析结果字段
     */
    private fun validateParseResult(result: AIParseResult): Boolean {
        // 验证优先级字段
        val validPriorities = listOf("高", "中", "低")
        if (result.Priority !in validPriorities) {
            return false
        }
        
        return true
    }

    /**
     * 更新日程记录
     */
    suspend fun updateSchedule(schedule: ScheduleEntity) {
        val updatedSchedule = schedule.copy(updatedAt = System.currentTimeMillis())
        scheduleDao.update(updatedSchedule)
    }

    /**
     * 删除日程记录
     */
    suspend fun deleteSchedule(schedule: ScheduleEntity) {
        scheduleDao.delete(schedule)
    }

    /**
     * 根据ID删除日程记录
     */
    suspend fun deleteScheduleById(id: Long) {
        scheduleDao.deleteById(id)
    }

    /**
     * 搜索日程
     */
    suspend fun searchSchedules(keyword: String): List<ScheduleEntity> {
        return scheduleDao.search(keyword)
    }

    /**
     * 删除所有日程
     */
    suspend fun deleteAllSchedules() {
        scheduleDao.deleteAll()
    }
}
