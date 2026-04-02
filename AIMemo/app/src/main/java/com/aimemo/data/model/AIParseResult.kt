package com.aimemo.data.model

/**
 * AI解析结果数据模型
 * 用于接收GLM API返回的JSON数据
 */
data class AIParseResult(
    val Event: String,
    val Time: String,
    val Location: String,
    val Priority: String
)

/**
 * GLM API 请求体模型
 */
data class GLMRequest(
    val model: String = "glm-4",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

/**
 * 消息模型
 */
data class Message(
    val role: String,
    val content: String
)

/**
 * GLM API 响应模型
 */
data class GLMResponse(
    val id: String?,
    val model: String?,
    val choices: List<Choice>?,
    val usage: Usage?
)

/**
 * 选项模型
 */
data class Choice(
    val index: Int?,
    val message: Message?,
    val finish_reason: String?
)

/**
 * 使用量统计
 */
data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)
