# 🌐 WovenLight GV

一款基于 Android 的 WebView 增强应用，支持 URL 管理、User Agent 切换和虚拟位置模拟功能。

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Material%20Design%203-007FFF?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Version](https://img.shields.io/badge/Version-1.1.0-blue?style=for-the-badge)](CHANGELOG.md)

---

## ✨ 核心功能

### 🌍 URL 管理
- 支持添加、编辑、删除多个 URL 配置
- 快速切换不同 URL 源
- 支持选择默认加载的 URL

### 🧭 User Agent 切换
- 内置系统默认 User Agent
- 支持自定义 User Agent 字符串
- 快速切换不同 UA 配置
- 实时生效，无需重启

### 📍 虚拟位置模拟
- 支持自定义经纬度坐标
- 可添加多个位置配置
- 一键切换虚拟位置
- 通过 JavaScript 注入实现位置欺骗
- 支持真实位置与虚拟位置切换

### 🎨 主题系统
- Material Design 3 设计
- 支持动态颜色 (Android 12+ Material You)
- 支持深色/浅色模式自动切换
- 系统壁纸颜色提取

---

## 🛠️ 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 1.9.24 |
| 构建工具 | Gradle | 8.5 |
| Android Gradle Plugin | AGP | 8.2.2 |
| UI 框架 | Jetpack Compose | 1.7.0 |
| 设计语言 | Material Design 3 | 1.3.0 |
| 序列化 | org.json | Android 内置 |

---

## 📱 系统要求

- **最低版本**: Android 5.0 (API 21)
- **目标版本**: Android 14 (API 36)
- **推荐版本**: Android 12+ (支持动态颜色)

---

## 🚀 使用说明

1. **首次使用**
   - 打开应用，点击"配置"按钮进入设置界面
   - 在 URL 管理中添加需要访问的 URL
   - 可选：添加自定义 User Agent
   - 可选：启用虚拟位置并添加坐标配置
   - 返回主界面，应用将自动加载选中的 URL

2. **快捷切换**
   - 点击底部"快捷切换"按钮，可快速切换：
     - URL 配置
     - User Agent
     - 虚拟位置开关及坐标

3. **刷新功能**
   - **单击刷新按钮**: 刷新当前页面
   - **双击刷新按钮**: 重新加载原始 URL

---

## 📁 项目结构

```
WovenLight-GV/
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
│   │   ├── res/                            # 资源文件
│   │   └── AndroidManifest.xml             # 清单文件
│   ├── build.gradle                        # 模块构建配置
│   └── proguard-rules.pro                  # ProGuard规则
├── wiki/                                   # 项目文档
│   ├── README.md
│   ├── ARCHITECTURE.md
│   ├── MODULES.md
│   ├── DATA_MODELS.md
│   ├── API.md
│   ├── DEVELOPMENT.md
│   └── SUMMARY.md
├── build.gradle                            # 项目构建配置
├── settings.gradle                         # 项目设置
└── README.md                               # 项目说明
```

---

## 📄 许可证

本项目使用 [MIT License](LICENSE)。

---

**Made with ❤️ by MXSZTY**