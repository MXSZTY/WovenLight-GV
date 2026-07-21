# WovenLight GV - 开发指南

## 1. 开发环境配置

### 1.1 系统要求

| 工具 | 版本要求 | 用途 |
|------|---------|------|
| Android Studio | Hedgehog (2023.1.1) 或更高 | IDE |
| JDK | 8 或更高 | Java 开发工具包 |
| Android SDK | API 36 | Android SDK |
| Gradle | 8.5 (自动下载) | 构建工具 |
| Kotlin | 1.9.24 (自动配置) | 编程语言 |

### 1.2 安装 Android Studio

1. 下载 Android Studio: https://developer.android.com/studio
2. 安装并启动 Android Studio
3. 完成初始设置向导（SDK 下载等）

### 1.3 配置 Android SDK

1. 打开 Android Studio
2. 进入 `Tools` → `SDK Manager`
3. 安装以下组件：
   - Android SDK Platform 36 (Android 14)
   - Android SDK Build-Tools 34
   - Android SDK Platform-Tools
   - Android SDK Command-line Tools

### 1.4 克隆项目

```bash
# 如果项目在 Git 仓库中
git clone <repository-url>
cd "WovenLight GV"

# 或直接打开本地项目
```

### 1.5 导入项目

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择项目根目录（包含 `build.gradle` 的目录）
4. 等待 Gradle 同步完成

---

## 2. 项目结构

### 2.1 目录结构

```
WovenLight GV/
├── app/                              # 应用模块
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mxszty/wovenlight/
│   │       │   ├── MainComposeActivity.kt
│   │       │   ├── SettingsScreen.kt
│   │       │   ├── manager/
│   │       │   │   └── DataManager.kt
│   │       │   └── model/
│   │       │       ├── UrlItem.kt
│   │       │       ├── UserAgentItem.kt
│   │       │       └── CoordinateItem.kt
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   ├── values-night/
│   │       │   │   └── colors.xml
│   │       │   ├── xml/
│   │       │   │   └── network_security_config.xml
│   │       │   └── mipmap-*/
│   │       └── AndroidManifest.xml
│   ├── build.gradle                  # 模块级构建配置
│   └── proguard-rules.pro            # ProGuard 规则
├── gradle/                           # Gradle 配置
├── build.gradle                      # 项目级构建配置
└── settings.gradle                   # 项目设置
```

---

## 3. 构建与运行

### 3.1 构建 Debug APK

#### 使用 Android Studio
1. 选择 `Build` → `Make Project` (Ctrl+F9)
2. 或选择 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

#### 使用命令行
```bash
# Windows
gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```

**输出位置**: `app/build/outputs/apk/debug/app-debug.apk`

---

### 3.2 构建 Release APK

#### 使用 Android Studio
1. 选择 `Build` → `Generate Signed Bundle / APK`
2. 选择 `APK`
3. 创建或选择密钥库
4. 选择 `release` 构建变体
5. 完成构建

#### 使用命令行
```bash
# 需要先配置签名
gradlew assembleRelease
```

**输出位置**: `app/build/outputs/apk/release/app-release.apk`

---

### 3.3 安装到设备

#### 使用 Android Studio
1. 连接 Android 设备或启动模拟器
2. 点击运行按钮 (Shift+F10)

#### 使用命令行
```bash
# 列出连接的设备
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 4. 依赖管理

### 4.1 查看依赖

```bash
# 查看项目依赖树
gradlew app:dependencies

# 或在 Android Studio 中
# 打开 build.gradle 文件，查看 dependencies 块
```

### 4.2 添加新依赖

在 `app/build.gradle` 中添加：

```gradle
dependencies {
    // 示例：添加协程依赖
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // 示例：添加 ViewModel 依赖
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
}
```

然后同步 Gradle：点击右上角的 `Sync Now` 或 `File` → `Sync Project with Gradle Files`

---

## 5. 调试技巧

### 5.1 使用 Logcat

1. 打开 Android Studio 的 Logcat 面板
2. 选择应用包名：`com.mxszty.wovenlight`
3. 过滤日志级别和关键词

**示例：查看位置相关日志**
```
Log.d("LocationInterface", "Latitude: $latitude")
```

在 Logcat 中输入：`LocationInterface`

### 5.2 断点调试

1. 在代码行号左侧点击设置断点
2. 右键断点设置条件
3. 点击调试按钮 (Shift+F9)
4. 程序会在断点处暂停

### 5.3 WebView 调试

启用 WebView 调试：
```kotlin
WebView.setWebContentsDebuggingEnabled(true)
```

然后访问：`chrome://inspect` 在 Chrome 浏览器中调试 WebView

---

## 6. 代码规范

### 6.1 Kotlin 编码规范

#### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | `MainComposeActivity` |
| 函数名 | 小驼峰 | `setupWebView()` |
| 变量名 | 小驼峰 | `urlList` |
| 常量 | 全大写下划线 | `PREF_URL_LIST` |
| 数据类 | 大驼峰 | `UrlItem` |

#### 文件组织

```kotlin
package com.mxszty.wovenlight

// 导入语句（按字母排序）
import android.os.Bundle
import androidx.activity.ComponentActivity

// 类定义
class MainComposeActivity : ComponentActivity() {
    // 属性
    private lateinit var webView: WebView
    
    // 生命周期方法
    override fun onCreate(savedInstanceState: Bundle?) { }
    
    // 公共方法
    fun refreshData() { }
    
    // 私有方法
    private fun setupWebView() { }
    
    // 内部类
    inner class LocationInterface { }
}
```

### 6.2 Compose 编码规范

#### Composable 函数命名
- 使用大驼峰命名
- 以名词开头，描述 UI 元素
- 示例：`Material3App()`, `SettingsScreen()`

#### 状态管理
```kotlin
@Composable
fun MyScreen() {
    // 使用 remember 缓存状态
    var state by remember { mutableStateOf(initialValue) }
    
    // 使用 LaunchedEffect 执行副作用
    LaunchedEffect(key) {
        // 执行操作
    }
}
```

---

## 7. 版本控制

### 7.1 Git 配置

创建 `.gitignore` 文件（已自动生成）：
```
# Android Studio
.idea/
*.iml

# Gradle
.gradle/
build/

# 本地配置
local.properties

# 签名文件
*.jks
*.keystore
```

### 7.2 提交规范

**提交信息格式**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型 (type)**:
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

**示例**:
```
feat(location): 添加虚拟位置功能

- 添加 LocationInterface 类
- 实现位置注入逻辑
- 添加虚拟位置开关

Closes #123
```

---

## 8. 测试

### 8.1 单元测试

在 `app/src/test/java/` 下创建测试类：

```kotlin
@RunWith(JUnit4::class)
class DataManagerTest {
    @Test
    fun testAddUrl() {
        // 测试添加 URL 功能
    }
}
```

运行测试：
```bash
gradlew test
```

### 8.2 UI 测试

在 `app/src/androidTest/java/` 下创建测试类：

```kotlin
@RunWith(AndroidJUnit4::class)
class MainComposeActivityTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testUI() {
        composeTestRule.setContent {
            Material3App()
        }
        
        // 测试 UI 元素
        composeTestRule.onNodeWithText("配置").performClick()
    }
}
```

运行测试：
```bash
gradlew connectedAndroidTest
```

---

## 9. 性能优化

### 9.1 代码优化

- **避免对象分配**: 在 Composable 中使用 `remember` 缓存对象
- **延迟计算**: 使用 `derivedStateOf` 减少重组
- **协程使用**: 在 IO 线程执行数据操作

### 9.2 WebView 优化

```kotlin
// 启用硬件加速
webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

// 配置缓存
webSettings.cacheMode = WebSettings.LOAD_DEFAULT

// 预加载
webView.resumeTimers()
```

### 9.3 内存优化

- 及时释放资源
- 避免内存泄漏
- 使用 LeakCanary 检测内存泄漏

---

## 10. 发布准备

### 10.1 版本号管理

在 `app/build.gradle` 中更新：

```gradle
android {
    defaultConfig {
        versionCode 11        // 每次发布递增
        versionName "1.1.0"   // 语义化版本号
    }
}
```

### 10.2 ProGuard 配置

在 `app/proguard-rules.pro` 中添加规则：

```proguard
# 保留数据模型类
-keep class com.mxszty.wovenlight.model.** { *; }

# 保留 JavaScript 接口方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

### 10.3 签名配置

在 `app/build.gradle` 中配置：

```gradle
android {
    signingConfigs {
        release {
            storeFile file("release.jks")
            storePassword "your_password"
            keyAlias "your_alias"
            keyPassword "your_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

**安全建议**: 将签名配置放在 `keystore.properties` 文件中，并添加到 `.gitignore`

---

## 11. 常见问题

### 11.1 Gradle 同步失败

**解决方案**:
1. 清理项目：`Build` → `Clean Project`
2. 清除缓存：`File` → `Invalidate Caches / Restart`
3. 检查网络连接（下载依赖）

### 11.2 WebView 加载失败

**检查项**:
1. URL 是否正确
2. 网络权限是否添加
3. 网络安全配置是否正确

### 11.3 位置权限被拒绝

**解决方案**:
1. 检查权限声明
2. 运行时请求权限
3. 处理权限拒绝情况

---

## 12. 进阶开发

### 12.1 添加 ViewModel

```kotlin
class MainViewModel : ViewModel() {
    private val _urlList = mutableStateOf<List<UrlItem>>(emptyList())
    val urlList: State<List<UrlItem>> = _urlList
    
    fun loadUrlList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _urlList.value = DataManager.getUrlList(context)
        }
    }
}

// 在 Activity 中使用
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val urlList by viewModel.urlList
    
    LaunchedEffect(Unit) {
        viewModel.loadUrlList(LocalContext.current)
    }
}
```

### 12.2 添加依赖注入 (Hilt)

```kotlin
// 在 build.gradle 中添加
implementation 'com.google.dagger:hilt-android:2.48'
kapt 'com.google.dagger:hilt-compiler:2.48'

// 创建 Application 类
@HiltAndroidApp
class MyApp : Application()

// 在 Activity 中使用
@AndroidEntryPoint
class MainComposeActivity : ComponentActivity()
```

### 12.3 添加 Room 数据库

```kotlin
// 在 build.gradle 中添加
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
ksp 'androidx.room:room-compiler:2.6.1'

// 定义 Entity
@Entity
data class UrlEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String
)

// 定义 DAO
@Dao
interface UrlDao {
    @Query("SELECT * FROM UrlEntity")
    suspend fun getAll(): List<UrlEntity>
    
    @Insert
    suspend fun insert(url: UrlEntity)
}

// 定义 Database
@Database(entities = [UrlEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun urlDao(): UrlDao
}
```

---

## 13. 相关资源

### 13.1 官方文档

- [Android Developer](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin 官方文档](https://kotlinlang.org/docs/)
- [Material Design 3](https://m3.material.io/)

### 13.2 学习资源

- [Android Codelabs](https://codelabs.developers.google.com/)
- [Kotlin Koans](https://play.kotlinlang.org/koans/)
- [Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)

### 13.3 工具

- [Android Studio 下载](https://developer.android.com/studio)
- [ADB 命令参考](https://developer.android.com/studio/command-line/adb)
- [Gradle 用户指南](https://docs.gradle.org/)

---

## 14. 贡献指南

### 14.1 如何贡献

1. Fork 项目仓库
2. 创建特性分支：`git checkout -b feature/new-feature`
3. 提交更改：`git commit -m 'feat: add new feature'`
4. 推送分支：`git push origin feature/new-feature`
5. 提交 Pull Request

### 14.2 代码审查标准

- 遵循代码规范
- 添加必要的注释
- 编写单元测试
- 更新相关文档

---

## 15. 许可证

本项目由 MXSZTY 开发维护。