# WovenLight GV - 架构设计文档

## 1. 整体架构

WovenLight GV 采用 **单 Activity 架构**，基于 **Jetpack Compose** 构建 UI 层，使用 **SharedPreferences** 进行数据持久化。整体架构遵循 **单向数据流 (UDF)** 和 **状态提升** 原则。

### 1.1 架构层次图

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                    │
│  ┌──────────────────┐      ┌─────────────────┐          │
│  │ MainComposeActivity │ ←→ │ SettingsScreen  │          │
│  └──────────────────┘      └─────────────────┘          │
│           ↓                         ↓                     │
│    State Management            State Management           │
└─────────────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│                  Business Logic Layer                    │
│  ┌──────────────────────────────────────────────┐       │
│  │              DataManager (Singleton)          │       │
│  │  - URL Management                             │       │
│  │  - User Agent Management                      │       │
│  │  - Coordinate Management                      │       │
│  │  - Selection State Management                 │       │
│  └──────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│                   Data Layer                             │
│  ┌──────────────────┐      ┌─────────────────┐          │
│  │ SharedPreferences │      │  Data Models    │          │
│  │  (Persistence)    │ ←→   │  - UrlItem     │          │
│  │                   │      │  - UserAgentItem│          │
│  │                   │      │  - CoordinateItem│         │
│  └──────────────────┘      └─────────────────┘          │
└─────────────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│                 Platform Layer                           │
│  ┌──────────────────┐      ┌─────────────────┐          │
│  │    WebView        │      │ Location Service │         │
│  │  (Web Rendering)  │      │  (GPS Provider) │          │
│  └──────────────────┘      └─────────────────┘          │
└─────────────────────────────────────────────────────────┘
```

---

## 2. 核心组件

### 2.1 Activity 层

#### MainComposeActivity
**职责**: 应用唯一入口，负责 UI 容器、WebView 管理和位置服务集成。

**关键特性**:
- 继承自 `ComponentActivity`
- 使用 `setContent` 初始化 Compose UI
- 管理 WebView 实例生命周期
- 实现位置权限请求和处理
- 提供位置数据给 WebView 的 JavaScript 接口

**内部类**:
- `LocationInterface`: 提供给 WebView JavaScript 调用的位置接口

**关键方法**:
```kotlin
// 系统状态栏配置
fun setupSystemBars()

// WebView 配置
fun setupWebView(webView: WebView, userAgent: String)

// 注入位置脚本到 WebView
fun injectLocationScripts(webView: WebView?)

// 处理返回按键
fun handleBackPress()
```

---

### 2.2 UI 层

#### 2.2.1 MainComposeActivity.Material3App (Composable)
**职责**: 主界面 UI 组合函数，包含 WebView 显示区域、底部导航栏、快捷切换对话框等。

**关键 Composable**:
- `Scaffold`: 提供 Material 3 风格的页面结构
- `CenterAlignedTopAppBar`: 顶部标题栏
- `Card`: 底部三个功能按钮（关于、配置、快捷切换）
- `FloatingActionButton`: 刷新按钮
- `AndroidView`: 嵌入 WebView
- `AlertDialog`: 关于对话框和快捷切换对话框

**状态管理**:
```kotlin
// 数据列表状态
var urlList by remember { mutableStateOf<List<UrlItem>>(emptyList()) }
var uaList by remember { mutableStateOf<List<UserAgentItem>>(emptyList()) }
var coordList by remember { mutableStateOf<List<CoordinateItem>>(emptyList()) }

// UI 状态
var useVirtualLocation by remember { mutableStateOf(false) }
var showSettings by mutableStateOf(false)
var showQuickSwitchDialog by mutableStateOf(false)
```

#### 2.2.2 SettingsScreen (Composable)
**职责**: 配置界面，提供 URL、User Agent、坐标的管理功能。

**关键功能**:
- URL 的增删改查
- User Agent 的增删改查
- 坐标的增删改查
- 虚拟位置开关控制

**状态提升**:
- 通过 `onBack` 回调返回主界面
- 所有数据操作通过 `DataManager` 进行

---

### 2.3 业务逻辑层

#### DataManager (Singleton Object)
**职责**: 数据管理的唯一入口，封装所有数据存取逻辑。

**设计模式**: 单例模式 (Object Declaration)

**核心功能**:

| 功能分类 | 主要方法 |
|---------|---------|
| URL 管理 | `getUrlList`, `addUrl`, `updateUrl`, `deleteUrl`, `getSelectedUrl` |
| UA 管理 | `getUserAgentList`, `addUserAgent`, `updateUserAgent`, `deleteUserAgent`, `getSelectedUA` |
| 坐标管理 | `getCoordinateList`, `addCoordinate`, `updateCoordinate`, `deleteCoordinate`, `getSelectedCoord` |
| 选择状态 | `setSelectedUrlId`, `setSelectedUAId`, `setSelectedCoordId` |
| 虚拟位置 | `getUseVirtualLocation`, `setUseVirtualLocation` |

**数据持久化**:
- 使用 `SharedPreferences` 存储 JSON 字符串
- 键值对映射：
  - `url_list`: URL 列表 JSON
  - `ua_list`: User Agent 列表 JSON
  - `coord_list`: 坐标列表 JSON
  - `selected_url_id`: 选中的 URL ID
  - `selected_ua_id`: 选中的 UA ID
  - `selected_coord_id`: 选中的坐标 ID
  - `use_virtual_location`: 是否使用虚拟位置

---

### 2.4 数据模型层

#### UrlItem
```kotlin
data class UrlItem(
    val id: String,        // UUID
    val name: String,      // 显示名称
    val url: String        // URL 地址
)
```

#### UserAgentItem
```kotlin
data class UserAgentItem(
    val id: String,        // UUID
    val name: String,      // 显示名称
    val userAgent: String  // UA 字符串
)
```

#### CoordinateItem
```kotlin
data class CoordinateItem(
    val id: String,        // UUID
    val name: String,      // 显示名称
    val latitude: Double,  // 纬度
    val longitude: Double  // 经度
)
```

**序列化**: 所有数据模型使用 `org.json.JSONObject` 进行 JSON 序列化/反序列化。

---

## 3. 数据流

### 3.1 数据读取流程

```
用户打开应用
    ↓
MainComposeActivity.onCreate()
    ↓
LaunchedEffect(Unit) 触发
    ↓
调用 refreshData()
    ↓
DataManager.getUrlList(context)
DataManager.getUserAgentList(context)
DataManager.getCoordinateList(context)
    ↓
SharedPreferences.getString() 读取 JSON
    ↓
JSONArray 解析
    ↓
创建 List<UrlItem> 等对象
    ↓
更新 Compose State
    ↓
UI 重组显示
```

### 3.2 数据写入流程

```
用户在 SettingsScreen 点击保存
    ↓
验证输入数据
    ↓
DataManager.addUrl(context, name, url)
    ↓
读取现有列表
    ↓
添加新项 (UUID.randomUUID())
    ↓
JSONArray.put(item.toJson())
    ↓
SharedPreferences.edit { putString(...) }
    ↓
数据持久化完成
    ↓
调用 refreshLists() 更新 UI
```

### 3.3 WebView 位置欺骗流程

```
WebView 加载页面完成
    ↓
onPageFinished() 回调
    ↓
injectLocationScripts(webView)
    ↓
注入 JavaScript 代码
    ↓
覆盖 navigator.geolocation API
    ↓
页面调用 getCurrentPosition()
    ↓
JavaScript 调用 AndroidLocation.getLatitude()
    ↓
LocationInterface.getLatitude()
    ↓
检查 useVirtualLocation 标志
    ↓
┌─────────────┬──────────────┐
│ True        │ False         │
↓             ↓               ↓
返回虚拟坐标   返回真实 GPS 坐标
```

---

## 4. 依赖关系

### 4.1 模块依赖图

```
MainComposeActivity
    ├── depends on → DataManager
    ├── depends on → UrlItem, UserAgentItem, CoordinateItem
    └── depends on → WebView (Android Framework)

SettingsScreen
    ├── depends on → DataManager
    └── depends on → UrlItem, UserAgentItem, CoordinateItem

DataManager
    ├── depends on → SharedPreferences (Android Framework)
    ├── depends on → JSONArray (org.json)
    └── depends on → UrlItem, UserAgentItem, CoordinateItem

Data Models (UrlItem, UserAgentItem, CoordinateItem)
    └── depends on → JSONObject (org.json)
```

### 4.2 外部依赖

#### Jetpack Compose 依赖
```gradle
implementation 'androidx.activity:activity-compose:1.9.2'
implementation 'androidx.compose.ui:ui:1.7.0'
implementation 'androidx.compose.foundation:foundation:1.7.0'
implementation 'androidx.compose.material3:material3:1.3.0'
```

#### AndroidX 依赖
```gradle
implementation 'androidx.core:core-ktx:1.13.0'
```

#### Kotlin 依赖
```gradle
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24"
```

---

## 5. 设计决策

### 5.1 为什么选择单 Activity 架构？

**理由**:
1. **简化导航**: 应用只有两个主要界面（主界面和配置界面），通过状态切换即可
2. **性能优化**: 避免多 Activity 切换开销
3. **Compose 最佳实践**: Jetpack Compose 推荐单 Activity + 多 Composable 的架构

### 5.2 为什么使用 SharedPreferences 而不是 Room？

**理由**:
1. **数据简单**: 应用只存储简单的列表数据，不需要复杂查询
2. **数据量小**: URL、UA、坐标配置数量有限
3. **开发效率**: SharedPreferences 集成简单，代码量少
4. **性能充足**: 对于简单配置数据，JSON 序列化性能足够

### 5.3 为什么使用 org.json 而不是 kotlinx.serialization？

**理由**:
1. **无需额外插件**: Android 内置 org.json 库
2. **兼容性**: 不需要配置 Kotlin 序列化插件
3. **简单场景**: 数据模型简单，无需复杂序列化功能
4. **减小体积**: 避免引入额外依赖

### 5.4 为什么位置欺骗使用 JavaScript 注入？

**理由**:
1. **无需系统权限**: 不需要 Root 或系统签名
2. **兼容性好**: 适用于所有 WebView 场景
3. **实时切换**: 可动态切换虚拟/真实位置
4. **安全合规**: 不修改系统级位置服务

---

## 6. 扩展性设计

### 6.1 数据层扩展

**当前**: SharedPreferences + JSON

**扩展方案**:
1. **迁移到 Room**: 如需复杂查询和数据关系
2. **添加数据加密**: 使用 EncryptedSharedPreferences
3. **云同步**: 集成 Firebase 或自建后端

### 6.2 UI 层扩展

**当前**: 单 Activity + Compose

**扩展方案**:
1. **添加 ViewModel**: 引入 ViewModel 管理 UI 状态
2. **添加 Navigation**: 如需更多界面层次
3. **添加 Hilt**: 依赖注入框架，解耦 DataManager

### 6.3 功能扩展

**可扩展方向**:
1. **书签管理**: 在 URL 基础上添加分类和标签
2. **历史记录**: 添加浏览历史功能
3. **广告拦截**: 注入 JavaScript 过滤广告
4. **多窗口**: 支持多 WebView 实例

---

## 7. 性能考量

### 7.1 WebView 优化

- 启用 DOM Storage
- 配置缓存策略
- 支持混合内容

### 7.2 内存管理

- WebView 实例在 Activity 生命周期内管理
- 避免内存泄漏：使用 `inner class` 持有 Activity 引用

### 7.3 UI 性能

- Compose 重组优化：使用 `remember` 缓存状态
- 列表性能：使用 `LazyColumn` 虚拟化长列表

---

## 8. 安全性

### 8.1 网络安全

- **明文传输**: 通过 `network_security_config.xml` 允许 HTTP
- **混合内容**: 设置 `mixedContentMode` 为 `MIXED_CONTENT_ALWAYS_ALLOW`

### 8.2 JavaScript 安全

- **接口暴露**: 仅暴露必要的方法给 JavaScript
- **注解保护**: 使用 `@JavascriptInterface` 注解限制调用

### 8.3 位置权限

- **运行时请求**: 在 WebView 需要位置时才请求权限
- **权限检查**: 每次位置获取前检查权限状态

---

## 9. 测试策略

### 9.1 单元测试

- **DataManager 测试**: 验证数据存取逻辑
- **Model 测试**: 验证 JSON 序列化/反序列化

### 9.2 UI 测试

- **Compose 测试**: 使用 `ComposeTestRule` 测试 UI 状态
- **交互测试**: 测试按钮点击、列表滚动等

### 9.3 集成测试

- **WebView 测试**: 验证位置欺骗功能
- **权限测试**: 验证运行时权限请求流程

---

## 10. 未来优化方向

1. **架构演进**: 引入 ViewModel + Hilt + Room
2. **性能监控**: 添加性能分析工具
3. **日志系统**: 集成 Timber 或自建日志系统
4. **崩溃收集**: 集成 Crashlytics
5. **自动化测试**: 增加测试覆盖率