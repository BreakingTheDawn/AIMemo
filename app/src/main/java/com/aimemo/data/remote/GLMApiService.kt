package com.aimemo.data.remote

import com.aimemo.data.model.GLMRequest
import com.aimemo.data.model.GLMResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * GLM API 服务接口
 * 定义与智谱AI GLM模型的通信接口
 */
interface GLMApiService {

    /**
     * 调用GLM模型进行文本解析
     * @param authorization Bearer token认证
     * @param request 请求体
     * @return GLM响应
     */
    @POST("chat/completions")
    suspend fun parseText(
        @Header("Authorization") authorization: String,
        @Body request: GLMRequest
    ): Response<GLMResponse>

    companion object {
        const val BASE_URL = "https://open.bigmodel.cn/api/paas/v4/"
    }
}
