# WovenLight GV - 项目概述

## 项目简介

**WovenLight GV** 是一款基于 Android 平台的 WebView 增强应用，提供了 URL 管理、User Agent 切换、虚拟位置模拟等核心功能。该应用采用 Jetpack Compose 构建 UI，支持动态主题和 Material Design 3 设计语言。

- **应用名称**: WovenLight
- **包名**: com.mxszty.wovenlight
- **当前版本**: 1.1.0 (versionCode: 11)
- **开发者**: MXSZTY
- **最低 SDK**: Android 5.0 (API 21)
- **目标 SDK**: Android 14 (API 36)

---

## 核心功能

### 1. URL 管理
- 支持添加、编辑、删除多个 URL 配置
- 快速切换不同 URL 源
- 支持选择默认加载的 URL

### 2. User Agent 管理
- 内置系统默认 User Agent
- 支持自定义 User Agent 字符串
- 快速切换不同 UA 配置
- 实时生效，无需重启

### 3. 虚拟位置模拟
- 支持自定义经纬度坐标
- 可添加多个位置配置
- 一键切换虚拟位置
- 通过 JavaScript 注入实现位置欺骗
- 支持真实位置与虚拟位置切换

### 4. WebView 增强
- 支持 JavaScript 交互
- 支持地理位置 API 拦截
- 支持 HTTP 明文传输
- 支持混合内容加载

### 5. 主题系统
- Material Design 3 设计
- 支持动态颜色 (Android 12+ Material You)
- 支持深色/浅色模式自动切换
- 系统壁纸颜色提取

---

## 技术栈

### 核心框架
- **Kotlin**: 1.9.24
- **Android Gradle Plugin**: 8.2.2
- **Gradle**: 8.5

### UI 框架
- **Jetpack Compose**: 1.7.0
- **Material 3**: 1.3.0
- **Activity Compose**: 1.9.2

### 架构组件
- **AndroidX Core**: 1.13.0
- **Lifecycle**: 集成于 Activity Compose

### 序列化
- **org.json**: Android 内置 JSON 库

---

## 项目结构

```
WovenLight GV/
├── app/
│   ├── src/main/
│   │   ├── java/com/mxszty/wovenlight/
│   │   │   ├── MainComposeActivity.kt      # 主界面Activity
│   │   │   ├── SettingsScreen.kt           # 配置界面
│   │   │   ├── manager/
│   │   │   │   └── DataManager.kt          # 数据管理器
│   │   │   └── model/
│   │   │       ├── UrlItem.kt              # URL数据模型
│   │   │       ├── UserAgentItem.kt        # UA数据模型
│   │   │       └── CoordinateItem.kt       # 坐标数据模型
│   │   ├── res/
│   │   │   ├── values/                     # 资源文件
│   │   │   ├── values-night/               # 深色模式资源
│   │   │   └── xml/                        # 配置文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

---

## 构建配置

### 应用级配置
```gradle
android {
    namespace 'com.mxszty.wovenlight'
    compileSdk 36
    
    defaultConfig {
        applicationId "com.mxszty.wovenlight"
        minSdkVersion 21
        targetSdkVersion 36
        versionCode 11
        versionName "1.1.0"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion "1.5.14"
    }
}
```

---

## 权限声明

| 权限 | 用途 |
|------|------|
| `INTERNET` | 网络访问 |
| `ACCESS_FINE_LOCATION` | 精确定位 |
| `ACCESS_COARSE_LOCATION` | 粗略定位 |

---

## 系统要求

- **最低版本**: Android 5.0 (Lollipop, API 21)
- **推荐版本**: Android 12+ (支持动态颜色)
- **目标版本**: Android 14 (API 36)

---

## 如何使用

### 首次使用
1. 打开应用，点击"配置"按钮进入设置界面
2. 在 URL 管理中添加需要访问的 URL
3. 可选：添加自定义 User Agent
4. 可选：启用虚拟位置并添加坐标配置
5. 返回主界面，应用将自动加载选中的 URL

### 快捷切换
点击底部"快捷切换"按钮，可快速切换：
- URL 配置
- User Agent
- 虚拟位置开关及坐标

### 刷新功能
- **单击刷新按钮**: 刷新当前页面
- **双击刷新按钮**: 重新加载原始 URL

---

## 构建与运行

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 8+
- Android SDK 36

### 构建步骤
```bash
# 清理项目
./gradlew clean

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

### 安装运行
```bash
# 安装到设备
./gradlew installDebug

# 或使用 adb
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 版本历史

### v1.1.0 (当前版本)
- 添加虚拟位置功能
- 支持 User Agent 管理
- 实现动态主题系统
- 优化 UI 交互体验
- 支持 Material Design 3

---

## 许可证

本项目由 MXSZTY 开发维护。

---

## 相关文档

- [架构设计文档](./ARCHITECTURE.md)
- [核心模块文档](./MODULES.md)
- [数据模型文档](./DATA_MODELS.md)
- [开发指南](./DEVELOPMENT.md)