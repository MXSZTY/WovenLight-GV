# WovenLight GV - 数据模型文档

## 1. 概述

WovenLight GV 使用三个数据模型来存储应用配置信息：

| 模型名 | 文件路径 | 用途 |
|--------|---------|------|
| UrlItem | `model/UrlItem.kt` | URL 配置数据 |
| UserAgentItem | `model/UserAgentItem.kt` | User Agent 配置数据 |
| CoordinateItem | `model/CoordinateItem.kt` | 虚拟位置坐标数据 |

所有数据模型均采用 **Kotlin Data Class** 定义，使用 **Android 内置 org.json 库**进行序列化和反序列化。

---

## 2. UrlItem

### 2.1 类定义

**文件**: [app/src/main/java/com/mxszty/wovenlight/model/UrlItem.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/model/UrlItem.kt)

```kotlin
data class UrlItem(
    val id: String,      // 唯一标识符 (UUID)
    val name: String,    // 显示名称
    val url: String      // URL 地址
)
```

### 2.2 属性说明

| 属性名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `id` | String | 唯一标识符，由 UUID 生成 | `"550e8400-e29b-41d4-a716-446655440000"` |
| `name` | String | 用户定义的显示名称 | `"Google"` |
| `url` | String | 完整的 URL 地址 | `"https://www.google.com"` |

### 2.3 序列化方法

#### toJson()

**签名**:
```kotlin
fun toJson(): JSONObject
```

**功能**: 将 UrlItem 转换为 JSONObject

**实现**:
```kotlin
fun toJson(): JSONObject {
    val json = JSONObject()
    json.put("id", id)
    json.put("name", name)
    json.put("url", url)
    return json
}
```

**示例**:
```kotlin
val urlItem = UrlItem(
    id = "550e8400-e29b-41d4-a716-446655440000",
    name = "Google",
    url = "https://www.google.com"
)

val json = urlItem.toJson()
// {"id":"550e8400-e29b-41d4-a716-446655440000","name":"Google","url":"https://www.google.com"}
```

#### fromJson()

**签名**:
```kotlin
companion object {
    fun fromJson(json: JSONObject): UrlItem
}
```

**功能**: 从 JSONObject 创建 UrlItem 实例

**实现**:
```kotlin
companion object {
    fun fromJson(json: JSONObject): UrlItem {
        return UrlItem(
            id = json.getString("id"),
            name = json.getString("name"),
            url = json.getString("url")
        )
    }
}
```

**示例**:
```kotlin
val json = JSONObject()
json.put("id", "550e8400-e29b-41d4-a716-446655440000")
json.put("name", "Google")
json.put("url", "https://www.google.com")

val urlItem = UrlItem.fromJson(json)
// UrlItem(id=550e8400-e29b-41d4-a716-446655440000, name=Google, url=https://www.google.com)
```

---

## 3. UserAgentItem

### 3.1 类定义

**文件**: [app/src/main/java/com/mxszty/wovenlight/model/UserAgentItem.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/model/UserAgentItem.kt)

```kotlin
data class UserAgentItem(
    val id: String,         // 唯一标识符 (UUID)
    val name: String,       // 显示名称
    val userAgent: String   // User Agent 字符串
)
```

### 3.2 属性说明

| 属性名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `id` | String | 唯一标识符，由 UUID 生成 | `"550e8400-e29b-41d4-a716-446655440001"` |
| `name` | String | 用户定义的显示名称 | `"Chrome Desktop"` |
| `userAgent` | String | 完整的 User Agent 字符串 | `"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"` |

### 3.3 常见 User Agent 示例

#### 桌面浏览器
```kotlin
// Chrome Desktop
UserAgentItem(
    id = UUID.randomUUID().toString(),
    name = "Chrome Desktop",
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
)

// Firefox Desktop
UserAgentItem(
    id = UUID.randomUUID().toString(),
    name = "Firefox Desktop",
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0"
)

// Safari Desktop
UserAgentItem(
    id = UUID.randomUUID().toString(),
    name = "Safari Desktop",
    userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_2) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15"
)
```

#### 移动浏览器
```kotlin
// Chrome Mobile
UserAgentItem(
    id = UUID.randomUUID().toString(),
    name = "Chrome Mobile",
    userAgent = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
)

// Safari iOS
UserAgentItem(
    id = UUID.randomUUID().toString(),
    name = "Safari iOS",
    userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1"
)
```

### 3.4 序列化方法

#### toJson()

```kotlin
fun toJson(): JSONObject {
    val json = JSONObject()
    json.put("id", id)
    json.put("name", name)
    json.put("userAgent", userAgent)
    return json
}
```

#### fromJson()

```kotlin
companion object {
    fun fromJson(json: JSONObject): UserAgentItem {
        return UserAgentItem(
            id = json.getString("id"),
            name = json.getString("name"),
            userAgent = json.getString("userAgent")
        )
    }
}
```

---

## 4. CoordinateItem

### 4.1 类定义

**文件**: [app/src/main/java/com/mxszty/wovenlight/model/CoordinateItem.kt](file:///c:/Users/mxszt/Documents/WovenLight%20GV/app/src/main/java/com/mxszty/wovenlight/model/CoordinateItem.kt)

```kotlin
data class CoordinateItem(
    val id: String,        // 唯一标识符 (UUID)
    val name: String,      // 显示名称
    val latitude: Double,  // 纬度
    val longitude: Double  // 经度
)
```

### 4.2 属性说明

| 属性名 | 类型 | 说明 | 取值范围 | 示例 |
|--------|------|------|---------|------|
| `id` | String | 唯一标识符 | UUID | `"550e8400-e29b-41d4-a716-446655440002"` |
| `name` | String | 显示名称 | 任意字符串 | `"北京天安门"` |
| `latitude` | Double | 纬度 | -90.0 ~ 90.0 | `39.9042` |
| `longitude` | Double | 经度 | -180.0 ~ 180.0 | `116.4074` |

### 4.3 常见坐标示例

```kotlin
// 北京天安门
CoordinateItem(
    id = UUID.randomUUID().toString(),
    name = "北京天安门",
    latitude = 39.9042,
    longitude = 116.4074
)

// 上海外滩
CoordinateItem(
    id = UUID.randomUUID().toString(),
    name = "上海外滩",
    latitude = 31.2304,
    longitude = 121.4737
)

// 纽约时代广场
CoordinateItem(
    id = UUID.randomUUID().toString(),
    name = "纽约时代广场",
    latitude = 40.7580,
    longitude = -73.9855
)

// 东京塔
CoordinateItem(
    id = UUID.randomUUID().toString(),
    name = "东京塔",
    latitude = 35.6586,
    longitude = 139.7454
)

// 悉尼歌剧院
CoordinateItem(
    id = UUID.randomUUID().toString(),
    name = "悉尼歌剧院",
    latitude = -33.8568,
    longitude = 151.2153
)
```

### 4.4 序列化方法

#### toJson()

```kotlin
fun toJson(): JSONObject {
    val json = JSONObject()
    json.put("id", id)
    json.put("name", name)
    json.put("latitude", latitude)
    json.put("longitude", longitude)
    return json
}
```

#### fromJson()

```kotlin
companion object {
    fun fromJson(json: JSONObject): CoordinateItem {
        return CoordinateItem(
            id = json.getString("id"),
            name = json.getString("name"),
            latitude = json.getDouble("latitude"),
            longitude = json.getDouble("longitude")
        )
    }
}
```

---

## 5. 数据存储格式

### 5.1 SharedPreferences 存储

所有数据以 JSON 数组形式存储在 SharedPreferences 中：

| 键名 | 存储内容 | 默认值 |
|------|---------|--------|
| `url_list` | URL 列表 JSON | `"[]"` |
| `ua_list` | User Agent 列表 JSON | `"[]"` |
| `coord_list` | 坐标列表 JSON | `"[]"` |

### 5.2 JSON 存储示例

#### url_list
```json
[
    {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "Google",
        "url": "https://www.google.com"
    },
    {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "Baidu",
        "url": "https://www.baidu.com"
    }
]
```

#### ua_list
```json
[
    {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "name": "Chrome Desktop",
        "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    }
]
```

#### coord_list
```json
[
    {
        "id": "550e8400-e29b-41d4-a716-446655440003",
        "name": "北京天安门",
        "latitude": 39.9042,
        "longitude": 116.4074
    }
]
```

---

## 6. 选择状态存储

除了数据列表，应用还存储当前选中的项目 ID：

| 键名 | 存储内容 | 默认值 | 类型 |
|------|---------|--------|------|
| `selected_url_id` | 选中的 URL ID | `""` | String |
| `selected_ua_id` | 选中的 UA ID | `""` (空字符串表示系统默认) | String |
| `selected_coord_id` | 选中的坐标 ID | `""` | String |
| `use_virtual_location` | 是否使用虚拟位置 | `false` | Boolean |

---

## 7. 数据操作示例

### 7.1 添加数据

```kotlin
// 添加 URL
DataManager.addUrl(context, "Google", "https://www.google.com")

// 添加 User Agent
DataManager.addUserAgent(context, "Chrome Desktop", "Mozilla/5.0 ...")

// 添加坐标
DataManager.addCoordinate(context, "北京天安门", 39.9042, 116.4074)
```

### 7.2 更新数据

```kotlin
// 更新 URL
DataManager.updateUrl(context, id, "Google CN", "https://www.google.cn")

// 更新 User Agent
DataManager.updateUserAgent(context, id, "Chrome Mobile", "Mozilla/5.0 ...")

// 更新坐标
DataManager.updateCoordinate(context, id, "故宫", 39.9163, 116.3972)
```

### 7.3 删除数据

```kotlin
// 删除 URL
DataManager.deleteUrl(context, id)

// 删除 User Agent
DataManager.deleteUserAgent(context, id)

// 删除坐标
DataManager.deleteCoordinate(context, id)
```

### 7.4 查询数据

```kotlin
// 获取所有 URL
val urlList = DataManager.getUrlList(context)

// 获取选中的 URL
val selectedUrl = DataManager.getSelectedUrl(context)

// 获取所有坐标
val coordList = DataManager.getCoordinateList(context)

// 获取选中的坐标
val selectedCoord = DataManager.getSelectedCoord(context)
```

---

## 8. 数据验证

### 8.1 输入验证建议

#### URL 验证
```kotlin
fun isValidUrl(url: String): Boolean {
    return try {
        val uri = URL(url)
        uri.protocol == "http" || uri.protocol == "https"
    } catch (e: Exception) {
        false
    }
}
```

#### 坐标验证
```kotlin
fun isValidCoordinate(lat: Double, lng: Double): Boolean {
    return lat in -90.0..90.0 && lng in -180.0..180.0
}
```

### 8.2 数据完整性

- **ID 唯一性**: 使用 `UUID.randomUUID().toString()` 确保唯一性
- **非空检查**: 在 UI 层验证必填字段
- **异常处理**: JSON 解析时使用 `try-catch` 捕获异常

---

## 9. 数据迁移与扩展

### 9.1 添加新字段

如需在数据模型中添加新字段：

1. 修改 Data Class 定义
2. 更新 `toJson()` 方法
3. 更新 `fromJson()` 方法，提供默认值处理旧数据

```kotlin
data class UrlItem(
    val id: String,
    val name: String,
    val url: String,
    val description: String = ""  // 新增字段
)

companion object {
    fun fromJson(json: JSONObject): UrlItem {
        return UrlItem(
            id = json.getString("id"),
            name = json.getString("name"),
            url = json.getString("url"),
            description = json.optString("description", "")  // 兼容旧数据
        )
    }
}
```

### 9.2 数据导出与导入

```kotlin
// 导出所有配置
fun exportConfig(context: Context): String {
    val json = JSONObject()
    json.put("urls", JSONArray(DataManager.getUrlList(context).map { it.toJson() }))
    json.put("uas", JSONArray(DataManager.getUserAgentList(context).map { it.toJson() }))
    json.put("coords", JSONArray(DataManager.getCoordinateList(context).map { it.toJson() }))
    return json.toString()
}

// 导入配置
fun importConfig(context: Context, jsonString: String) {
    val json = JSONObject(jsonString)
    // 解析并保存数据...
}
```

---

## 10. 最佳实践

### 10.1 数据安全

- **敏感信息**: 避免在 URL 或坐标名称中存储敏感信息
- **加密**: 如需加密，使用 `EncryptedSharedPreferences`

### 10.2 性能优化

- **批量操作**: 修改多个项目时，先读取整个列表，修改后一次性保存
- **缓存**: DataManager 是单例，避免重复读取 SharedPreferences

### 10.3 错误处理

```kotlin
try {
    val urlList = DataManager.getUrlList(context)
    // 处理数据
} catch (e: JSONException) {
    // 处理 JSON 解析错误
    Log.e("DataModel", "Failed to parse JSON", e)
}
```