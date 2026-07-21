# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2026-07-21

### Added

- ✨ **URL 管理功能**
  - 支持添加、编辑、删除多个 URL 配置
  - 快速切换不同 URL 源
  - 支持选择默认加载的 URL

- ✨ **User Agent 切换功能**
  - 内置系统默认 User Agent
  - 支持自定义 User Agent 字符串
  - 快速切换不同 UA 配置

- ✨ **虚拟位置模拟功能**
  - 支持自定义经纬度坐标
  - 可添加多个位置配置
  - 通过 JavaScript 注入实现位置欺骗
  - 支持真实位置与虚拟位置切换

- ✨ **主题系统**
  - Material Design 3 设计风格
  - 支持动态颜色 (Android 12+ Material You)
  - 支持深色/浅色模式自动切换

- ✨ **WebView 增强功能**
  - 自定义 WebView 配置
  - JavaScript 交互支持
  - 页面刷新和重载功能

### Changed

- 🔄 重构项目架构，采用单一 Activity + Jetpack Compose 模式
- 🔄 使用 SharedPreferences 进行数据持久化
- 🔄 使用 org.json 进行 JSON 序列化/反序列化

### Fixed

- 🐛 修复 WebView 加载空白页面问题
- 🐛 修复配置数据保存失败问题

### Security

- 🔒 使用 `@JavascriptInterface` 注解限制 JavaScript 接口
- 🔒 添加网络安全配置