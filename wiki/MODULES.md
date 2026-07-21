# WovenLight GV - 核心模块文档

## 1. 模块概览

WovenLight GV 项目由以下核心模块组成：

| 模块名 | 路径 | 职责 |
|--------|------|------|
| 主界面模块 | `MainComposeActivity.kt` | 应用主入口，UI 容器，WebView 管理 |
| 配置界面模块 | `SettingsScreen.kt` | URL/UA/坐标配置管理界面 |
| 数据管理模块 | `manager/DataManager.kt` | 数据持久化逻辑封装 |
| 数据模型模块 | `model/*.kt` | 数据结构定义 |

---

## 2. 主界面模块 (MainComposeActivity)

### 2.1 类定义

**文件**: [app/src/main/java/com/mxszty/wovenlight/MainComposeActivity.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/MainComposeActivity.kt)

```kotlin
class MainComposeActivity : ComponentActivity()
```

**职责**:
- 应用唯一 Activity，负责 UI 容器和生命周期管理
- 管理 WebView 实例
- 提供位置服务接口给 WebView
- 处理系统状态栏和导航栏

---

### 2.2 核心内部类

#### LocationInterface

```kotlin
inner class LocationInterface
```

**功能**: 提供给 WebView JavaScript 调用的位置接口

**注解方法**:

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `getLatitude()` | Double | 获取纬度 |
| `getLongitude()` | Double | 获取经度 |
| `getUseVirtualLocation()` | Boolean | 是否使用虚拟位置 |
| `showToast(message: String)` | Unit | 显示 Toast 消息 |

**核心逻辑**:

```kotlin
@JavascriptInterface
fun getLatitude(): Double {
    val useVirtualLocation = DataManager.getUseVirtualLocation(this@MainComposeActivity)
    
    if (useVirtualLocation) {
        val coord = DataManager.getSelectedCoord(this@MainComposeActivity)
        if (coord != null) {
            return coord.latitude
        }
        return 39.9042  // 默认：北京
    }
    
    // 返回真实 GPS 坐标
    if (cachedLatitude == null) {
        handler.post { refreshLocationCache() }
    }
    return cachedLatitude ?: 39.9042
}
```

---

### 2.3 核心方法详解

#### setupWebView()

**签名**:
```kotlin
@SuppressLint("SetJavaScriptEnabled")
private fun setupWebView(webView: WebView, userAgent: String)
```

**功能**: 配置 WebView 实例

**关键配置**:
- `javaScriptEnabled = true`: 启用 JavaScript
- `domStorageEnabled = true`: 启用 DOM 存储
- `mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW`: 允许混合内容
- `setGeolocationEnabled(true)`: 启用地理位置
- `userAgentString = userAgent`: 设置 User Agent

**示例**:
```kotlin
webView.webChromeClient = object : WebChromeClient() {
    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        // 检查并请求位置权限
        if (ContextCompat.checkSelfPermission(
                this@MainComposeActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            callback?.invoke(origin, true, false)
        } else {
            ActivityCompat.requestPermissions(...)
        }
    }
}
```

---

#### injectLocationScripts()

**签名**:
```kotlin
private fun injectLocationScripts(webView: WebView?)
```

**功能**: 注入 JavaScript 代码覆盖 `navigator.geolocation` API

**核心逻辑**:
```kotlin
webView?.evaluateJavascript("""
    (function() {
        // 保存原始方法
        var originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
        
        // 覆盖 getCurrentPosition
        navigator.geolocation.getCurrentPosition = function(success, error, options) {
            // 调用 Android 接口
            var useVirtual = window.AndroidLocation.getUseVirtualLocation();
            
            if (useVirtual) {
                // 返回虚拟位置
                var lat = window.AndroidLocation.getLatitude();
                var lng = window.AndroidLocation.getLongitude();
                success(createPosition(lat, lng));
            } else {
                // 调用原始方法
                originalGetCurrentPosition.call(navigator.geolocation, success, error, options);
            }
        };
    })();
""".trimIndent(), null)
```

---

#### setupSystemBars()

**签名**:
```kotlin
private fun setupSystemBars()
```

**功能**: 配置系统状态栏和导航栏

**实现细节**:
- 设置透明状态栏和导航栏
- 根据背景色自动调整状态栏图标颜色
- 支持动态颜色 (Android 12+)

```kotlin
WindowCompat.setDecorFitsSystemWindows(window, false)

val surfaceColor = if (Build.VERSION.SDK_INT >= 31) {
    ContextCompat.getColor(this, android.R.color.system_neutral1_100)
} else {
    ContextCompat.getColor(this, R.color.m3_surface)
}

val insetsController = WindowCompat.getInsetsController(window, window.decorView)
insetsController.isAppearanceLightStatusBars = isLightColor(surfaceColor)
```

---

### 2.4 Composable 函数

#### Material3App()

**签名**:
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3App()
```

**功能**: 主界面 UI 组合函数

**UI 结构**:
```
Scaffold
├── topBar: CenterAlignedTopAppBar (显示 "URL名称-UA名称")
├── bottomBar: Row (底部三个按钮)
│   ├── Card: "关于" 按钮
│   ├── Card: "配置" 按钮
│   └── Card: "快捷切换" 按钮
├── floatingActionButton: FloatingActionButton (刷新按钮)
└── content: Box
    ├── 未配置时: 显示 "请先配置软件"
    └── 已配置时: Card + AndroidView (WebView)
```

**关键状态**:
```kotlin
// 数据状态
var urlList by remember { mutableStateOf<List<UrlItem>>(emptyList()) }
var uaList by remember { mutableStateOf<List<UserAgentItem>>(emptyList()) }
var coordList by remember { mutableStateOf<List<CoordinateItem>>(emptyList()) }

// UI 状态
var showAboutDialog by remember { mutableStateOf(false) }
var showQuickSwitchDialog by remember { mutableStateOf(false) }

// 刷新回调
LaunchedEffect(showSettings) {
    if (!showSettings) {
        refreshData()
        if (::webView.isInitialized) {
            webView.reload()
        }
    }
}
```

---

## 3. 配置界面模块 (SettingsScreen)

### 3.1 Composable 函数

**文件**: [app/src/main/java/com/mxszty/wovenlight/SettingsScreen.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/SettingsScreen.kt)

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit)
```

**参数**:
- `onBack: () -> Unit`: 返回主界面的回调

---

### 3.2 UI 结构

```
Scaffold
├── topBar: TopAppBar
│   ├── title: "配置"
│   └── navigationIcon: 返回按钮
└── content: LazyColumn
    ├── Card: URL 管理
    │   ├── 标题 + 添加按钮
    │   └── URL 列表 (编辑/删除按钮)
    ├── Card: User Agent 管理
    │   ├── 标题 + 添加按钮
    │   ├── 系统默认选项
    │   └── UA 列表 (编辑/删除按钮)
    └── Card: 虚拟位置管理
        ├── 开关: 使用虚拟位置
        └── 坐标管理 (条件显示)
            ├── 标题 + 添加按钮
            └── 坐标列表 (编辑/删除按钮)
```

---

### 3.3 核心功能

#### 数据刷新

```kotlin
fun refreshLists() {
    urlList = DataManager.getUrlList(context)
    uaList = DataManager.getUserAgentList(context)
    coordList = DataManager.getCoordinateList(context)
    useVirtualLocation = DataManager.getUseVirtualLocation(context)
    selectedUrlId = DataManager.getSelectedUrlId(context)
    selectedUaId = DataManager.getSelectedUAId(context)
    selectedCoordId = DataManager.getSelectedCoordId(context)
}
```

#### URL 增删改查

```kotlin
// 添加
Button(onClick = {
    if (urlNameInput.isNotEmpty() && urlInput.isNotEmpty()) {
        DataManager.addUrl(context, urlNameInput, urlInput)
        refreshLists()
        showUrlDialog = false
    }
})

// 编辑
Button(onClick = {
    if (editingUrl != null) {
        DataManager.updateUrl(context, editingUrl!!.id, urlNameInput, urlInput)
    }
    refreshLists()
})

// 删除
IconButton(onClick = {
    DataManager.deleteUrl(context, item.id)
    refreshLists()
})
```

---

### 3.4 对话框

#### URL 对话框

```kotlin
if (showUrlDialog) {
    AlertDialog(
        onDismissRequest = { showUrlDialog = false },
        title = { Text(if (editingUrl != null) "编辑URL" else "添加URL") },
        text = {
            Column {
                OutlinedTextField(
                    value = urlNameInput,
                    onValueChange = { urlNameInput = it },
                    label = { Text("名称") }
                )
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("URL") }
                )
            }
        },
        confirmButton = { /* 保存逻辑 */ },
        dismissButton = { /* 取消逻辑 */ }
    )
}
```

---

## 4. 数据管理模块 (DataManager)

### 4.1 单例对象

**文件**: [app/src/main/java/com/mxszty/wovenlight/manager/DataManager.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/manager/DataManager.kt)

```kotlin
object DataManager
```

**设计模式**: 单例模式 (Object Declaration)

---

### 4.2 常量定义

```kotlin
private const val PREFS_NAME = "AppData"
private const val PREF_URL_LIST = "url_list"
private const val PREF_UA_LIST = "ua_list"
private const val PREF_COORD_LIST = "coord_list"
private const val PREF_SELECTED_URL_ID = "selected_url_id"
private const val PREF_SELECTED_UA_ID = "selected_ua_id"
private const val PREF_SELECTED_COORD_ID = "selected_coord_id"
private const val PREF_USE_VIRTUAL_LOCATION = "use_virtual_location"
```

---

### 4.3 URL 管理方法

#### getUrlList()

**签名**:
```kotlin
fun getUrlList(context: Context): List<UrlItem>
```

**返回**: URL 配置列表

**实现**:
```kotlin
val jsonString = getPrefs(context).getString(PREF_URL_LIST, "[]")
return try {
    val jsonArray = JSONArray(jsonString)
    val list = mutableListOf<UrlItem>()
    for (i in 0 until jsonArray.length()) {
        list.add(UrlItem.fromJson(jsonArray.getJSONObject(i)))
    }
    list
} catch (_: JSONException) {
    emptyList()
}
```

---

#### addUrl()

**签名**:
```kotlin
fun addUrl(context: Context, name: String, url: String)
```

**功能**: 添加新的 URL 配置

**实现**:
```kotlin
val list = getUrlList(context).toMutableList()
list.add(UrlItem(UUID.randomUUID().toString(), name, url))
saveUrlList(context, list)
```

---

#### updateUrl()

**签名**:
```kotlin
fun updateUrl(context: Context, id: String, name: String, url: String)
```

**功能**: 更新指定 ID 的 URL 配置

---

#### deleteUrl()

**签名**:
```kotlin
fun deleteUrl(context: Context, id: String)
```

**功能**: 删除指定 ID 的 URL 配置

**附加逻辑**: 如果删除的是当前选中的 URL，自动选择列表中第一个

```kotlin
if (getSelectedUrlId(context) == id) {
    setSelectedUrlId(context, list.firstOrNull()?.id ?: "")
}
```

---

### 4.4 User Agent 管理方法

#### getUserAgentList()

**签名**:
```kotlin
fun getUserAgentList(context: Context): List<UserAgentItem>
```

---

#### addUserAgent()

**签名**:
```kotlin
fun addUserAgent(context: Context, name: String, userAgent: String)
```

---

#### updateUserAgent()

**签名**:
```kotlin
fun updateUserAgent(context: Context, id: String, name: String, userAgent: String)
```

---

#### deleteUserAgent()

**签名**:
```kotlin
fun deleteUserAgent(context: Context, id: String)
```

---

### 4.5 坐标管理方法

#### getCoordinateList()

**签名**:
```kotlin
fun getCoordinateList(context: Context): List<CoordinateItem>
```

---

#### addCoordinate()

**签名**:
```kotlin
fun addCoordinate(context: Context, name: String, latitude: Double, longitude: Double)
```

---

#### updateCoordinate()

**签名**:
```kotlin
fun updateCoordinate(context: Context, id: String, name: String, latitude: Double, longitude: Double)
```

---

#### deleteCoordinate()

**签名**:
```kotlin
fun deleteCoordinate(context: Context, id: String)
```

---

### 4.6 选择状态管理

#### getSelectedUrlId() / setSelectedUrlId()

```kotlin
fun getSelectedUrlId(context: Context): String
fun setSelectedUrlId(context: Context, id: String)
```

---

#### getSelectedUrl()

```kotlin
fun getSelectedUrl(context: Context): UrlItem?
```

**实现**:
```kotlin
val id = getSelectedUrlId(context)
return getUrlList(context).firstOrNull { it.id == id }
```

---

### 4.7 虚拟位置管理

#### getUseVirtualLocation() / setUseVirtualLocation()

```kotlin
fun getUseVirtualLocation(context: Context): Boolean
fun setUseVirtualLocation(context: Context, enabled: Boolean)
```

---

## 5. 工具方法

### getPrefs()

```kotlin
private fun getPrefs(context: Context) = 
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
```

---

### saveUrlList()

```kotlin
fun saveUrlList(context: Context, list: List<UrlItem>) {
    val jsonArray = JSONArray()
    list.forEach { jsonArray.put(it.toJson()) }
    getPrefs(context).edit { putString(PREF_URL_LIST, jsonArray.toString()) }
}
```

---

## 6. 状态管理

### 6.1 状态提升原则

所有 UI 状态通过 `remember { mutableStateOf(...) }` 管理在 Composable 函数中，数据操作通过 `DataManager` 单例进行。

### 6.2 状态更新流程

```
用户操作 (点击/输入)
    ↓
调用 DataManager 方法
    ↓
数据持久化到 SharedPreferences
    ↓
调用 refreshLists() 刷新状态
    ↓
Compose 检测到 State 变化
    ↓
触发 UI 重组
```

---

## 7. 最佳实践

### 7.1 数据操作建议

- **批量操作**: 在进行多次数据操作时，先读取整个列表，修改后再一次性保存
- **错误处理**: 使用 `try-catch` 处理 JSON 解析异常
- **ID 生成**: 使用 `UUID.randomUUID().toString()` 确保唯一性

### 7.2 状态管理建议

- **避免直接修改**: 不要直接修改从 DataManager 获取的列表，应创建副本
- **及时刷新**: 数据修改后立即调用 `refreshLists()` 更新 UI
- **状态提升**: 将状态提升到合适的层级，避免在深层 Composable 中管理状态