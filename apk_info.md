# AI Memo - Release APK 信息

## APK文件信息

### Debug版本
- **文件名：** app-debug.apk
- **大小：** 15.8 MB
- **编译时间：** 2026-04-02 15:55:01
- **用途：** 开发调试

### Release版本
- **文件名：** app-release.apk
- **大小：** 1.46 MB
- **编译时间：** 2026-04-02 16:16:14
- **用途：** 正式发布

---

## 签名信息

### 签名证书详情
```
Signer #1 certificate DN: CN=AIMemo, OU=Development, O=AIMemo, L=Shenzhen, ST=Guangdong, C=CN
Signer #1 certificate SHA-256 digest: 4006c949696f488c596ef02cf0865a8f890886b3250af3589db7a15b4ae2d7d7
Signer #1 certificate SHA-1 digest: fd4d60b20df38cbcd5221f42c8fa91ff46319880
Signer #1 certificate MD5 digest: 1f43a31b4a7fc4b8a6e1c0d845066746
```

### 密钥库信息
- **文件名：** aimemo-release.keystore
- **别名：** aimemo
- **算法：** RSA 2048-bit
- **有效期：** 10,000 天
- **创建时间：** 2026-04-02

---

## 优化效果

### APK大小对比
| 版本 | 大小 | 说明 |
|------|------|------|
| Debug | 15.8 MB | 未优化，包含调试信息 |
| Release | 1.46 MB | 优化后，体积减少90.8% |

### 优化措施
1. **代码混淆（ProGuard/R8）**
   - 移除未使用的代码
   - 混淆类名、方法名
   - 优化字节码

2. **资源压缩**
   - 移除未使用的资源
   - 压缩资源文件
   - 优化图片资源

3. **代码优化**
   - 内联方法
   - 移除死代码
   - 优化常量

---

## 应用信息

### 基本信息
- **应用名称：** AI Memo
- **包名：** com.aimemo
- **版本号：** 1.0.0
- **版本代码：** 1
- **最低SDK：** Android 8.0 (API 26)
- **目标SDK：** Android 14 (API 34)

### 功能特性
- ✅ AI智能解析自然语言日程
- ✅ 本地持久化存储
- ✅ 系统日历集成
- ✅ 语音输入
- ✅ 日程提醒
- ✅ 深色模式
- ✅ Material 3设计

### 权限要求
```xml
<!-- 网络权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- 日历权限 -->
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />

<!-- 语音识别权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- 日程提醒权限 -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## 安装要求

### 系统要求
- Android 8.0 (API 26) 或更高版本
- 约 5MB 可用存储空间

### 安装方法
1. 将APK文件传输到Android设备
2. 在设备上打开APK文件
3. 允许安装来自未知来源的应用
4. 按照提示完成安装

---

## 使用说明

### 首次使用
1. 启动应用
2. 输入GLM API密钥（需要从智谱AI官网获取）
3. 开始使用语音或文本输入日程

### 核心功能
1. **文本输入：** 直接输入自然语言描述日程
2. **语音输入：** 点击麦克风按钮语音输入
3. **日程管理：** 点击日程卡片查看详情、编辑或删除
4. **系统日历：** 一键添加到系统日历
5. **日程提醒：** 设置提醒时间，到期自动通知

---

## 注意事项

### API密钥
- 需要GLM API密钥才能使用AI解析功能
- 密钥安全存储在本地，不会上传到服务器
- 请妥善保管您的API密钥

### 权限说明
- **网络权限：** 用于调用AI API
- **日历权限：** 用于添加日程到系统日历
- **录音权限：** 用于语音输入功能
- **通知权限：** 用于日程提醒通知

### 数据安全
- 所有数据存储在本地设备
- 不会上传到云端服务器
- 卸载应用会清除所有数据

---

## 技术支持

### 问题反馈
如遇到问题，请提供以下信息：
- 设备型号和Android版本
- 问题描述和复现步骤
- 错误截图或日志

### 已知问题
- 暂无已知问题

---

**文档创建时间：** 2026-04-02
**文档版本：** 1.0
