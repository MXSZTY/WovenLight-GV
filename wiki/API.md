# WovenLight GV - API 接口文档

## 1. 概述

WovenLight GV 提供两种类型的 API 接口：

1. **JavaScript 接口**: 提供给 WebView 中运行的 JavaScript 代码调用
2. **Kotlin 公共接口**: 提供给应用内部各模块调用

---

## 2. JavaScript 接口

### 2.1 LocationInterface

**接口对象**: `window.AndroidLocation`

**注入位置**: WebView JavaScript 上下文

**用途**: 提供位置信息访问能力

---

#### getLatitude()

**签名**:
```javascript
window.AndroidLocation.getLatitude(): Number
```

**功能**: 获取当前纬度

**返回值**:
- 类型: `Number`
- 说明: 根据虚拟位置开关返回真实或虚拟纬度
- 默认值: `39.9042` (北京)

**示例**:
```javascript
const lat = window.AndroidLocation.getLatitude();
console.log('Latitude:', lat);  // 输出: 39.9042
```

---

#### getLongitude()

**签名**:
```javascript
window.AndroidLocation.getLongitude(): Number
```

**功能**: 获取当前经度

**返回值**:
- 类型: `Number`
- 说明: 根据虚拟位置开关返回真实或虚拟经度
- 默认值: `116.4074` (北京)

**示例**:
```javascript
const lng = window.AndroidLocation.getLongitude();
console.log('Longitude:', lng);  // 输出: 116.4074
```

---

#### getUseVirtualLocation()

**签名**:
```javascript
window.AndroidLocation.getUseVirtualLocation(): Boolean
```

**功能**: 检查是否启用虚拟位置

**返回值**:
- 类型: `Boolean`
- `true`: 使用虚拟位置
- `false`: 使用真实位置

**示例**:
```javascript
const useVirtual = window.AndroidLocation.getUseVirtualLocation();
if (useVirtual) {
    console.log('Using virtual location');
} else {
    console.log('Using real location');
}
```

---

#### showToast()

**签名**:
```javascript
window.AndroidLocation.showToast(message: String): void
```

**功能**: 显示 Android Toast 消息

**参数**:
- `message`: 要显示的消息文本

**示例**:
```javascript
window.AndroidLocation.showToast('Hello from JavaScript!');
```

---

### 2.2 完整使用示例

#### 获取位置信息

```javascript
// 检查接口是否存在
if (window.AndroidLocation) {
    const lat = window.AndroidLocation.getLatitude();
    const lng = window.AndroidLocation.getLongitude();
    const useVirtual = window.AndroidLocation.getUseVirtualLocation();
    
    console.log(`Location: (${lat}, ${lng})`);
    console.log(`Virtual Location: ${useVirtual ? 'Enabled' : 'Disabled'}`);
    
    // 显示位置信息
    window.AndroidLocation.showToast(`Current location: ${lat}, ${lng}`);
} else {
    console.error('AndroidLocation interface not available');
}
```

---

## 3. Kotlin 公共接口

### 3.1 DataManager API

#### URL 管理

##### getUrlList()

**签名**:
```kotlin
fun getUrlList(context: Context): List<UrlItem>
```

**功能**: 获取所有 URL 配置

**参数**:
- `context: Context`: Android 上下文对象

**返回值**:
- 类型: `List<UrlItem>`
- 说明: URL 配置列表，可能为空列表

**示例**:
```kotlin
val urlList = DataManager.getUrlList(context)
urlList.forEach { item ->
    println("${item.name}: ${item.url}")
}
```

---

##### addUrl()

**签名**:
```kotlin
fun addUrl(context: Context, name: String, url: String)
```

**功能**: 添加新的 URL 配置

**参数**:
- `context: Context`: Android 上下文对象
- `name: String`: URL 显示名称
- `url: String`: URL 地址

**返回值**: 无

**示例**:
```kotlin
DataManager.addUrl(context, "Google", "https://www.google.com")
```

---

##### updateUrl()

**签名**:
```kotlin
fun updateUrl(context: Context, id: String, name: String, url: String)
```

**功能**: 更新指定 URL 配置

**参数**:
- `context: Context`: Android 上下文对象
- `id: String`: URL 唯一标识符
- `name: String`: 新的显示名称
- `url: String`: 新的 URL 地址

**返回值**: 无

**示例**:
```kotlin
DataManager.updateUrl(context, urlId, "Google CN", "https://www.google.cn")
```

---

##### deleteUrl()

**签名**:
```kotlin
fun deleteUrl(context: Context, id: String)
```

**功能**: 删除指定 URL 配置

**参数**:
- `context: Context`: Android 上下文对象
- `id: String`: URL 唯一标识符

**返回值**: 无

**副作用**: 如果删除的是当前选中的 URL，自动选择列表中第一个

**示例**:
```kotlin
DataManager.deleteUrl(context, urlId)
```

---

##### getSelectedUrlId() / setSelectedUrlId()

**签名**:
```kotlin
fun getSelectedUrlId(context: Context): String
fun setSelectedUrlId(context: Context, id: String)
```

**功能**: 获取或设置当前选中的 URL ID

**示例**:
```kotlin
// 获取选中的 URL ID
val selectedId = DataManager.getSelectedUrlId(context)

// 设置选中的 URL
DataManager.setSelectedUrlId(context, newUrlId)
```

---

##### getSelectedUrl()

**签名**:
```kotlin
fun getSelectedUrl(context: Context): UrlItem?
```

**功能**: 获取当前选中的 URL 配置对象

**返回值**:
- 类型: `UrlItem?`
- 说明: 选中的 URL 对象，可能为 null

**示例**:
```kotlin
val selectedUrl = DataManager.getSelectedUrl(context)
if (selectedUrl != null) {
    webView.loadUrl(selectedUrl.url)
}
```

---

#### User Agent 管理

##### getUserAgentList()

**签名**:
```kotlin
fun getUserAgentList(context: Context): List<UserAgentItem>
```

**功能**: 获取所有 User Agent 配置

---

##### addUserAgent()

**签名**:
```kotlin
fun addUserAgent(context: Context, name: String, userAgent: String)
```

**功能**: 添加新的 User Agent 配置

---

##### updateUserAgent()

**签名**:
```kotlin
fun updateUserAgent(context: Context, id: String, name: String, userAgent: String)
```

**功能**: 更新指定 User Agent 配置

---

##### deleteUserAgent()

**签名**:
```kotlin
fun deleteUserAgent(context: Context, id: String)
```

**功能**: 删除指定 User Agent 配置

---

##### getSelectedUAId() / setSelectedUAId()

**签名**:
```kotlin
fun getSelectedUAId(context: Context): String
fun setSelectedUAId(context: Context, id: String)
```

**功能**: 获取或设置当前选中的 User Agent ID

**注意**: 空字符串表示使用系统默认 User Agent

---

##### getSelectedUA()

**签名**:
```kotlin
fun getSelectedUA(context: Context): UserAgentItem?
```

**功能**: 获取当前选中的 User Agent 配置对象

---

#### 坐标管理

##### getCoordinateList()

**签名**:
```kotlin
fun getCoordinateList(context: Context): List<CoordinateItem>
```

**功能**: 获取所有坐标配置

---

##### addCoordinate()

**签名**:
```kotlin
fun addCoordinate(context: Context, name: String, latitude: Double, longitude: Double)
```

**功能**: 添加新的坐标配置

**参数**:
- `context: Context`: Android 上下文对象
- `name: String`: 坐标显示名称
- `latitude: Double`: 纬度 (-90.0 ~ 90.0)
- `longitude: Double`: 经度 (-180.0 ~ 180.0)

---

##### updateCoordinate()

**签名**:
```kotlin
fun updateCoordinate(context: Context, id: String, name: String, latitude: Double, longitude: Double)
```

**功能**: 更新指定坐标配置

---

##### deleteCoordinate()

**签名**:
```kotlin
fun deleteCoordinate(context: Context, id: String)
```

**功能**: 删除指定坐标配置

---

##### getSelectedCoordId() / setSelectedCoordId()

**签名**:
```kotlin
fun getSelectedCoordId(context: Context): String
fun setSelectedCoordId(context: Context, id: String)
```

**功能**: 获取或设置当前选中的坐标 ID

---

##### getSelectedCoord()

**签名**:
```kotlin
fun getSelectedCoord(context: Context): CoordinateItem?
```

**功能**: 获取当前选中的坐标配置对象

---

#### 虚拟位置管理

##### getUseVirtualLocation() / setUseVirtualLocation()

**签名**:
```kotlin
fun getUseVirtualLocation(context: Context): Boolean
fun setUseVirtualLocation(context: Context, enabled: Boolean)
```

**功能**: 获取或设置是否启用虚拟位置

**示例**:
```kotlin
// 检查虚拟位置状态
val useVirtual = DataManager.getUseVirtualLocation(context)

// 启用虚拟位置
DataManager.setUseVirtualLocation(context, true)
```

---

## 4. WebView 配置 API

### 4.1 setupWebView()

**签名**:
```kotlin
@SuppressLint("SetJavaScriptEnabled")
private fun setupWebView(webView: WebView, userAgent: String)
```

**功能**: 配置 WebView 实例

**配置项**:

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `javaScriptEnabled` | `true` | 启用 JavaScript |
| `domStorageEnabled` | `true` | 启用 DOM 存储 |
| `allowFileAccess` | `true` | 允许文件访问 |
| `allowContentAccess` | `true` | 允许内容访问 |
| `useWideViewPort` | `true` | 使用宽视口 |
| `loadWithOverviewMode` | `true` | 概览模式加载 |
| `cacheMode` | `LOAD_DEFAULT` | 默认缓存模式 |
| `setGeolocationEnabled(true)` | - | 启用地理位置 |
| `mixedContentMode` | `MIXED_CONTENT_ALWAYS_ALLOW` | 允许混合内容 |
| `userAgentString` | 参数传入 | 自定义 User Agent |

---

## 5. 系统状态栏 API

### 5.1 setupSystemBars()

**签名**:
```kotlin
private fun setupSystemBars()
```

**功能**: 配置系统状态栏和导航栏

**效果**:
- 设置透明状态栏和导航栏
- 根据背景色自动调整状态栏图标颜色
- 支持动态颜色 (Android 12+)

---

## 6. 位置注入 API

### 6.1 injectLocationScripts()

**签名**:
```kotlin
private fun injectLocationScripts(webView: WebView?)
```

**功能**: 注入 JavaScript 代码覆盖 `navigator.geolocation` API

**注入内容**:
```javascript
(function() {
    // 保存原始方法
    var originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
    var originalWatchPosition = navigator.geolocation.watchPosition;
    var originalClearWatch = navigator.geolocation.clearWatch;
    
    // 覆盖 getCurrentPosition
    navigator.geolocation.getCurrentPosition = function(success, error, options) {
        var useVirtual = window.AndroidLocation.getUseVirtualLocation();
        if (useVirtual) {
            var lat = window.AndroidLocation.getLatitude();
            var lng = window.AndroidLocation.getLongitude();
            success(createPosition(lat, lng));
        } else {
            originalGetCurrentPosition.call(navigator.geolocation, success, error, options);
        }
    };
    
    // 覆盖 watchPosition
    navigator.geolocation.watchPosition = function(success, error, options) {
        // 类似 getCurrentPosition 的逻辑
    };
    
    // 覆盖 clearWatch
    navigator.geolocation.clearWatch = function(watchId) {
        originalClearWatch.call(navigator.geolocation, watchId);
    };
})();
```

---

## 7. 返回按键处理 API

### 7.1 handleBackPress()

**签名**:
```kotlin
private fun handleBackPress()
```

**功能**: 处理返回按键逻辑

**优先级**:
1. 如果在配置界面，关闭配置界面
2. 如果 WebView 可以后退，执行后退
3. 否则退出应用

---

## 8. 使用示例

### 8.1 完整初始化流程

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取选中的配置
        val selectedUrl = DataManager.getSelectedUrl(this)
        val selectedUa = DataManager.getSelectedUA(this)
        
        // 创建 WebView
        webView = WebView(this)
        setupWebView(webView, selectedUa?.userAgent ?: "")
        
        // 加载 URL
        if (selectedUrl != null) {
            webView.loadUrl(selectedUrl.url)
        }
    }
}
```

### 8.2 JavaScript 调用示例

```html
<!DOCTYPE html>
<html>
<head>
    <title>Location Demo</title>
    <script>
        function getLocation() {
            if (window.AndroidLocation) {
                var lat = window.AndroidLocation.getLatitude();
                var lng = window.AndroidLocation.getLongitude();
                document.getElementById('location').innerText = 
                    'Location: ' + lat + ', ' + lng;
            } else {
                navigator.geolocation.getCurrentPosition(function(pos) {
                    document.getElementById('location').innerText = 
                        'Location: ' + pos.coords.latitude + ', ' + pos.coords.longitude;
                });
            }
        }
    </script>
</head>
<body>
    <h1>Location Demo</h1>
    <button onclick="getLocation()">Get Location</button>
    <p id="location"></p>
</body>
</html>
```

---

## 9. 错误处理

### 9.1 JavaScript 错误处理

```javascript
function safeGetLocation() {
    try {
        if (!window.AndroidLocation) {
            throw new Error('AndroidLocation not available');
        }
        
        const lat = window.AndroidLocation.getLatitude();
        const lng = window.AndroidLocation.getLongitude();
        
        if (typeof lat !== 'number' || typeof lng !== 'number') {
            throw new Error('Invalid coordinate values');
        }
        
        return { lat, lng };
    } catch (e) {
        console.error('Failed to get location:', e.message);
        return null;
    }
}
```

### 9.2 Kotlin 错误处理

```kotlin
fun safeGetUrlList(context: Context): List<UrlItem> {
    return try {
        DataManager.getUrlList(context)
    } catch (e: Exception) {
        Log.e("API", "Failed to get URL list", e)
        emptyList()
    }
}
```

---

## 10. 安全性注意事项

### 10.1 JavaScript 接口安全

- **注解限制**: 只有 `@JavascriptInterface` 注解的方法才能被 JavaScript 调用
- **参数验证**: 所有从 JavaScript 接收的参数应进行验证
- **异常捕获**: 接口方法应捕获所有异常，避免应用崩溃

### 10.2 数据访问安全

- **权限检查**: 访问位置信息前检查权限
- **空值检查**: 处理可空返回值时进行空值检查
- **数据验证**: 存储数据前验证数据有效性