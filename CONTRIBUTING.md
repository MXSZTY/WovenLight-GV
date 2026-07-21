# 🤝 贡献指南

欢迎贡献代码！无论您是修复 Bug、添加新功能还是改进文档，我们都非常欢迎您的参与。

---

## 📋 贡献流程

### 1. Fork 项目

点击 GitHub 页面右上角的 **Fork** 按钮，将项目 Fork 到您的账户。

### 2. 克隆项目

```bash
git clone https://github.com/your-username/WovenLight-GV.git
cd WovenLight-GV
```

### 3. 创建分支

创建一个新分支来开发您的功能或修复：

```bash
# 功能分支
git checkout -b feature/new-feature

# Bug 修复分支
git checkout -b fix/bug-description
```

### 4. 开发

- 编写代码
- 添加测试（如果适用）
- 更新文档（如果适用）

### 5. 提交代码

使用规范的提交信息格式：

```bash
git commit -m '类型(范围): 简短描述'
```

**提交类型**:
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整（不影响功能）
- `refactor`: 重构（不改变功能）
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

### 6. 推送分支

```bash
git push origin feature/new-feature
```

### 7. 创建 Pull Request

在 GitHub 页面上点击 **Compare & pull request** 按钮，创建 Pull Request。

---

## 📝 代码规范

### Kotlin 编码规范

- **命名**:
  - 类名：大驼峰 (`MainComposeActivity`)
  - 函数名：小驼峰 (`setupWebView`)
  - 变量名：小驼峰 (`urlList`)
  - 常量：全大写下划线 (`PREF_URL_LIST`)

- **文件组织**:
  - 按功能模块组织文件
  - 保持文件简洁，单个文件不超过 500 行
  - 使用清晰的包结构

- **注释**:
  - 为公共函数和类添加文档注释
  - 复杂逻辑添加行内注释
  - 使用英文注释

### Jetpack Compose 规范

- Composable 函数使用大驼峰命名
- 使用 `remember` 缓存状态
- 使用 `LaunchedEffect` 执行副作用
- 避免在 Composable 中执行耗时操作

### JavaScript 接口规范

- 所有暴露给 JavaScript 的方法必须使用 `@JavascriptInterface` 注解
- 验证所有从 JavaScript 接收的参数
- 捕获所有异常，避免应用崩溃

---

## ✅ 代码审查标准

在提交 Pull Request 前，请确保：

1. ✅ 代码符合编码规范
2. ✅ 添加了必要的注释和文档
3. ✅ 编写了单元测试（如果适用）
4. ✅ 更新了相关文档
5. ✅ 项目能够正常构建
6. ✅ 没有引入新的警告

---

## 🐛 报告问题

如果发现 Bug 或有功能建议，请：

1. 搜索现有的 Issues，看看是否已经有人报告
2. 创建新 Issue，提供详细信息：
   - 问题描述
   - 复现步骤
   - 预期行为
   - 实际行为
   - 设备信息（Android 版本、设备型号）
   - 截图或日志（如果适用）

---

## 📄 文档贡献

改进文档也是非常有价值的贡献！您可以：

1. 完善现有文档
2. 添加新的文档
3. 修复文档中的错误
4. 添加使用示例

文档位于 `wiki/` 目录，使用 Markdown 格式编写。

---

## 📌 注意事项

- **不要提交敏感信息**: 不要在代码或提交中包含 API Key、密码等敏感信息
- **遵守许可证**: 本项目使用 MIT 许可证，请确保您的贡献符合许可证要求
- **尊重他人工作**: 尊重其他贡献者的工作，保持友好的沟通

---

感谢您的贡献！🎉

**Made with ❤️ by MXSZTY**