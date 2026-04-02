# AI Memo - Release APK 打包说明

## 签名配置

### 方案一：使用调试签名（仅用于测试）
如果只是用于测试，可以使用默认的debug签名打包Release APK：

```bash
./gradlew assembleRelease
```

生成的APK位于：`app/build/outputs/apk/release/app-release-unsigned.apk`

**注意：** 未签名的APK无法在大多数设备上安装，仅用于内部测试。

### 方案二：创建正式签名（推荐用于发布）

#### 步骤1：生成签名密钥
```bash
keytool -genkey -v -keystore aimemo-release.keystore \
  -alias aimemo \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

#### 步骤2：配置签名信息
在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("aimemo-release.keystore")
            storePassword = "YOUR_STORE_PASSWORD"
            keyAlias = "aimemo"
            keyPassword = "YOUR_KEY_PASSWORD"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

#### 步骤3：打包Release APK
```bash
./gradlew assembleRelease
```

生成的APK位于：`app/build/outputs/apk/release/app-release.apk`

---

## 安全建议

### 密码管理
**⚠️ 重要：** 不要将签名密钥密码提交到版本控制系统！

推荐做法：
1. 将 `aimemo-release.keystore` 添加到 `.gitignore`
2. 使用环境变量或 `local.properties` 存储密码
3. 在CI/CD中使用加密的密钥库

### 使用local.properties配置
在 `local.properties` 中添加：
```properties
storeFile=aimemo-release.keystore
storePassword=YOUR_STORE_PASSWORD
keyAlias=aimemo
keyPassword=YOUR_KEY_PASSWORD
```

在 `build.gradle.kts` 中读取：
```kotlin
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("storeFile") ?: "aimemo-release.keystore")
            storePassword = localProperties.getProperty("storePassword") ?: ""
            keyAlias = localProperties.getProperty("keyAlias") ?: ""
            keyPassword = localProperties.getProperty("keyPassword") ?: ""
        }
    }
}
```

---

## ProGuard配置

当前项目已启用ProGuard代码混淆，配置文件位于 `app/proguard-rules.pro`。

### 已配置的混淆规则
```proguard
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.aimemo.data.model.**$$serializer { *; }
-keepclassmembers class com.aimemo.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.aimemo.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# 应用数据模型
-keep class com.aimemo.data.model.** { *; }
-keep class com.aimemo.data.entity.** { *; }
```

---

## 打包命令

### Debug版本
```bash
./gradlew assembleDebug
```

### Release版本
```bash
./gradlew assembleRelease
```

### 清理并重新打包
```bash
./gradlew clean assembleRelease
```

### 查看APK信息
```bash
# 查看APK签名信息
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk

# 查看APK内容
unzip -l app/build/outputs/apk/release/app-release.apk
```

---

## 发布检查清单

- [ ] 更新版本号（versionCode和versionName）
- [ ] 检查ProGuard规则是否完整
- [ ] 确认签名密钥安全存储
- [ ] 测试Release APK在真机上运行
- [ ] 检查应用大小是否合理
- [ ] 验证所有功能正常工作
- [ ] 准备发布说明（Release Notes）

---

**文档创建时间：** 2026-04-02
**文档版本：** 1.0
