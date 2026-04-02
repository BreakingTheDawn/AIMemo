package com.aimemo.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 语音识别管理器
 * 封装Android原生SpeechRecognizer，提供语音识别功能
 * 
 * 使用流程：
 * 1. 检查权限后调用startListening()
 * 2. 监听recognitionState和recognizedText状态
 * 3. 使用完毕后调用destroy()
 */
class VoiceRecognitionManager(private val context: Context) {

    // 语音识别器实例
    private var speechRecognizer: SpeechRecognizer? = null

    // 识别状态
    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    // 识别结果文本
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 识别状态密封类
     */
    sealed class RecognitionState {
        object Idle : RecognitionState()           // 空闲状态
        object Listening : RecognitionState()      // 正在监听
        object Processing : RecognitionState()     // 正在处理
        object Completed : RecognitionState()      // 识别完成
        data class Error(val code: Int) : RecognitionState()  // 发生错误
    }

    /**
     * 初始化语音识别器
     */
    private fun initSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _recognitionState.value = RecognitionState.Listening
                    }

                    override fun onBeginningOfSpeech() {
                        // 用户开始说话
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // 音量变化，可用于UI反馈
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                        // 接收到音频数据
                    }

                    override fun onEndOfSpeech() {
                        _recognitionState.value = RecognitionState.Processing
                    }

                    override fun onError(error: Int) {
                        _recognitionState.value = RecognitionState.Error(error)
                        _errorMessage.value = getErrorText(error)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _recognizedText.value = matches[0]
                            _recognitionState.value = RecognitionState.Completed
                        } else {
                            _recognitionState.value = RecognitionState.Error(-1)
                            _errorMessage.value = "未识别到语音内容"
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        // 部分识别结果
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _recognizedText.value = matches[0]
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // 其他事件
                    }
                })
            }
        }
    }

    /**
     * 开始语音识别
     */
    fun startListening() {
        // 检查设备是否支持语音识别
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _recognitionState.value = RecognitionState.Error(-1)
            _errorMessage.value = "当前设备不支持语音识别，请使用真机测试或安装 Google 语音服务"
            return
        }
        
        initSpeechRecognizer()
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您的日程内容")
        }
        
        _recognitionState.value = RecognitionState.Idle
        _recognizedText.value = ""
        _errorMessage.value = null
        
        speechRecognizer?.startListening(intent)
    }

    /**
     * 停止语音识别
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    /**
     * 取消语音识别
     */
    fun cancel() {
        speechRecognizer?.cancel()
        _recognitionState.value = RecognitionState.Idle
    }

    /**
     * 重置状态
     */
    fun reset() {
        _recognitionState.value = RecognitionState.Idle
        _recognizedText.value = ""
        _errorMessage.value = null
    }

    /**
     * 销毁语音识别器
     * 在Activity/Fragment销毁时调用
     */
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        _recognitionState.value = RecognitionState.Idle
    }

    /**
     * 获取错误描述文本
     * 提供更详细的错误说明和解决建议
     */
    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "音频录制错误，请检查麦克风是否正常"
            SpeechRecognizer.ERROR_CLIENT -> "客户端错误，请确保设备安装了 Google 语音服务，建议使用真机测试"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足，请授予麦克风权限"
            SpeechRecognizer.ERROR_NETWORK -> "网络错误，请检查网络连接"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时，请检查网络连接后重试"
            SpeechRecognizer.ERROR_NO_MATCH -> "未识别到语音，请靠近麦克风重新说话"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙碌，请稍后重试"
            SpeechRecognizer.ERROR_SERVER -> "服务器错误，请稍后重试"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "未检测到语音输入，请靠近麦克风说话"
            else -> "未知错误 ($errorCode)"
        }
    }

    companion object {
        /**
         * 检查设备是否支持语音识别
         */
        fun isAvailable(context: Context): Boolean {
            return SpeechRecognizer.isRecognitionAvailable(context)
        }
    }
}
