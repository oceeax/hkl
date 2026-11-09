# 开机亮屏 5 分钟（ScreenWake5）

开机后尝试启动带彩色几何图案的全屏页面，页面显示“屏幕已点亮”和倒计时；页面用 `FLAG_KEEP_SCREEN_ON` 保持显示，5 分钟后自动退出。服务另持有一个限时唤醒锁作为兼容性尝试。

## 重要限制
- 只点亮并显示锁屏界面，不会绕过 PIN/图案/密码。
- Android 10 及以上对后台直接启动 Activity 有限制；Android 14/15/16 也对前台服务启动和类型有限制。系统或 ColorOS 可能阻止开机广播启动页面/服务，因此不能保证重启后必然自动弹出。
- `SCREEN_BRIGHT_WAKE_LOCK` / `ACQUIRE_CAUSES_WAKEUP` 是过时 API，不同系统版本可能忽略。
- 首次安装后先手动打开并点“立即测试”，允许通知，再在 ColorOS 设置里允许自启动、后台运行，并按需关闭电池优化。
- 如手动测试成功，再重启验证开机行为。

## GitHub Actions 构建
1. 将项目全部文件上传到 GitHub 仓库根目录。
2. 打开 Actions，运行 `Build ScreenWake5 APK`。
3. 在成功记录的 Artifacts 下载 `ScreenWake5-debug`，解压得到 `app-debug.apk`。
