package com.aimemo.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.aimemo.util.VoiceRecognitionManager

/**
 * 语音输入按钮组件
 * 提供语音识别功能，支持权限请求和状态显示
 * 
 * @param onTextRecognized 识别成功回调
 * @param onError 发生错误回调
 * @param modifier 修饰符
 */
@Composable
fun VoiceInputButton(
    onTextRecognized: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 语音识别管理器
    val voiceManager = remember { VoiceRecognitionManager(context) }

    // 收集状态
    val recognitionState by voiceManager.recognitionState.collectAsState()
    val recognizedText by voiceManager.recognizedText.collectAsState()
    val errorMessage by voiceManager.errorMessage.collectAsState()

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening()
        } else {
            onError("需要麦克风权限才能使用语音输入功能")
        }
    }

    // 检查权限状态
    val hasRecordPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    // 处理识别结果
    LaunchedEffect(recognitionState) {
        when (recognitionState) {
            is VoiceRecognitionManager.RecognitionState.Completed -> {
                if (recognizedText.isNotEmpty()) {
                    onTextRecognized(recognizedText)
                    voiceManager.reset()
                }
            }
            is VoiceRecognitionManager.RecognitionState.Error -> {
                errorMessage?.let { onError(it) }
            }
            else -> {}
        }
    }

    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    // 动画效果 - 录音时脉冲
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val isListening = recognitionState is VoiceRecognitionManager.RecognitionState.Listening

    // 语音按钮
    Box(modifier = modifier) {
        // 录音时的背景动画
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(scale)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }

        FloatingActionButton(
            onClick = {
                if (hasRecordPermission) {
                    when (recognitionState) {
                        is VoiceRecognitionManager.RecognitionState.Idle,
                        is VoiceRecognitionManager.RecognitionState.Completed,
                        is VoiceRecognitionManager.RecognitionState.Error -> {
                            voiceManager.startListening()
                        }
                        is VoiceRecognitionManager.RecognitionState.Listening -> {
                            voiceManager.stopListening()
                        }
                        is VoiceRecognitionManager.RecognitionState.Processing -> {
                            // 处理中，不响应点击
                        }
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            modifier = Modifier.size(56.dp),
            containerColor = when (recognitionState) {
                is VoiceRecognitionManager.RecognitionState.Listening -> MaterialTheme.colorScheme.error
                is VoiceRecognitionManager.RecognitionState.Processing -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primaryContainer
            },
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = if (isListening) 8.dp else 4.dp
            )
        ) {
            Icon(
                imageVector = when (recognitionState) {
                    is VoiceRecognitionManager.RecognitionState.Listening -> Icons.Default.Mic
                    is VoiceRecognitionManager.RecognitionState.Processing -> Icons.Default.Mic
                    else -> Icons.Default.MicNone
                },
                contentDescription = when (recognitionState) {
                    is VoiceRecognitionManager.RecognitionState.Listening -> "正在录音，点击停止"
                    is VoiceRecognitionManager.RecognitionState.Processing -> "正在识别..."
                    else -> "点击开始语音输入"
                },
                tint = when (recognitionState) {
                    is VoiceRecognitionManager.RecognitionState.Listening -> Color.White
                    is VoiceRecognitionManager.RecognitionState.Processing -> Color.White
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 语音输入状态指示器
 * 显示当前语音识别状态
 */
@Composable
fun VoiceRecognitionIndicator(
    state: VoiceRecognitionManager.RecognitionState,
    modifier: Modifier = Modifier
) {
    when (state) {
        is VoiceRecognitionManager.RecognitionState.Listening -> {
            androidx.compose.material3.Text(
                text = "正在聆听...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier
            )
        }
        is VoiceRecognitionManager.RecognitionState.Processing -> {
            androidx.compose.material3.Text(
                text = "正在识别...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = modifier
            )
        }
        else -> {}
    }
}
