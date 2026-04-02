# AI Memo 智能日程记事本

一款基于 Android 原生开发的智能日程管理应用，通过 AI 自动解析自然语言文本，提取关键日程信息并生成本地日程卡片。

## 功能特性

- **智能解析**：输入自然语言文本，AI 自动提取事件、时间、地点、优先级
- **本地存储**：所有日程数据存储在本地 Room 数据库，保护隐私
- **Material 3 设计**：现代化 UI 界面，支持浅色/深色主题
- **便捷交互**：点击卡片查看详情、编辑、删除日程

## 技术栈

| 技术 | 说明 |
|------|------|
| Kotlin | 100% Kotlin 代码 |
| Jetpack Compose | 声明式 UI 框架 |
| Material 3 | 最新设计规范 |
| MVVM | ViewModel + Repository 架构 |
| Kotlin Coroutines | 异步处理 |
| Flow | 响应式数据流 |
| Room Database | 本地持久化存储 |
| Retrofit | 网络请求 |
| GLM API | 智谱 AI 大模型接口 |

## 项目结构

```
app/src/main/java/com/aimemo/
├── data/                    # 数据层
│   ├── dao/                 # 数据访问对象
│   ├── local/               # 本地数据源
│   ├── model/               # 数据模型
│   ├── remote/              # 远程数据源
│   └── repository/          # 数据仓库
├── ui/                      # UI 层
│   ├── components/          # 可复用组件
│   ├── screens/             # 页面
│   ├── theme/               # 主题配置
│   └── viewmodel/           # 视图模型
├── util/                    # 工具类
├── AIMemoApplication.kt     # Application 类
└── MainActivity.kt          # 主 Activity
```

## 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2

### 运行步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd AIMemo
   ```

2. **在 Android Studio 中打开**
   - 选择 `Open an Existing Project`
   - 选择 AIMemo 文件夹

3. **同步 Gradle**
   - 等待 Gradle 同步完成

4. **配置 API 密钥**
   - 运行应用后，点击右上角钥匙图标
   - 输入您的 GLM API 密钥
   - 获取密钥：[智谱 AI 开放平台](https://open.bigmodel.cn/)

5. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮

## 使用说明

### 添加日程

1. 在输入框中输入自然语言文本，例如：
   > 下周二早上10点在南山区科技园有个关于自驾游的产品会，记得带电脑

2. 点击「解析」按钮

3. AI 将自动提取：
   - **事件**：自驾游产品会
   - **时间**：2024-01-16 10:00
   - **地点**：南山区科技园
   - **优先级**：中

### 管理日程

- **查看详情**：点击日程卡片
- **编辑日程**：在详情对话框中点击「编辑」
- **删除日程**：在详情对话框中点击「删除」

## AI Prompt 设计

### 系统提示词

应用使用精心设计的系统提示词约束 AI 输出格式，确保返回标准 JSON：

```
你是一个专业的日程信息提取助手。你的任务是从用户输入的自然语言文本中提取关键日程信息，并以标准JSON格式返回。

## 输出格式要求
你必须严格返回以下JSON格式，不要添加任何其他文字、注释或markdown标记：
{
    "Event": "事件名称",
    "Time": "yyyy-MM-dd HH:mm",
    "Location": "地点信息",
    "Priority": "高/中/低"
}
...
```

### JSON Schema

```json
{
    "type": "object",
    "required": ["Event", "Time", "Location", "Priority"],
    "properties": {
        "Event": {
            "type": "string",
            "description": "提取的核心事件名称，简洁明确，不超过50字"
        },
        "Time": {
            "type": "string",
            "description": "事件的完整时间，统一转为yyyy-MM-dd HH:mm格式，无明确时间则返回空字符串"
        },
        "Location": {
            "type": "string",
            "description": "事件的地点信息，无明确地点则返回空字符串"
        },
        "Priority": {
            "type": "string",
            "enum": ["高", "中", "低"],
            "description": "事件优先级，无明确优先级默认返回「中」"
        }
    }
}
```

## 依赖版本

| 依赖 | 版本 |
|------|------|
| Compose BOM | 2023.10.01 |
| Room | 2.6.1 |
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Kotlinx Coroutines | 1.7.3 |
| DataStore | 1.0.0 |

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
