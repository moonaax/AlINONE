# ALLINONE 🚀

Compose Multiplatform 跨平台应用集合，目前包含两个模块：

- 🎵 **Music** — 聚合音乐平台，一个 App 听遍全网音乐
- 🎨 **UI Kit** — 现代美学组件库（开发中）

## 截图

> TODO: 添加截图

## 平台支持

| 平台 | 状态 | 运行命令 |
|------|------|---------|
| 🖥️ Desktop (macOS/Win/Linux) | ✅ 已适配 | `./gradlew :composeApp:run` |
| 🤖 Android | ✅ 已适配 | `./gradlew :composeApp:installDebug` |
| 🍎 iOS | 🚧 待验证 | Xcode 打开 iosApp 项目 |

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin Multiplatform | 2.1.0 | 跨平台共享逻辑 |
| Compose Multiplatform | 1.7.3 | 跨平台 UI |
| Ktor | 3.0.3 | 网络请求 |
| Kotlinx Serialization | 1.7.3 | JSON 解析 |
| Coil 3 | 3.0.4 | 跨平台图片加载 |
| Material 3 | — | 设计系统 |

## 项目结构

```
ALLINONE/
├── composeApp/
│   └── src/
│       ├── commonMain/        # 跨平台共享代码
│       │   └── kotlin/.../
│       │       ├── App.kt              # 应用入口 + 路由
│       │       ├── ui/
│       │       │   ├── LandingScreen.kt    # 首页双卡片导航
│       │       │   ├── MainScreen.kt       # 音乐主界面（Tab 导航）
│       │       │   ├── home/HomeScreen.kt  # 推荐歌单页
│       │       │   ├── search/SearchScreen.kt  # 搜索页
│       │       │   └── uikit/UIKitScreen.kt    # UI 组件库（占位）
│       │       └── data/
│       │           ├── api/KuwoApi.kt      # 酷我音乐 API
│       │           ├── api/HttpClientFactory.kt
│       │           ├── model/MusicModels.kt
│       │           └── repository/MusicRepository.kt
│       ├── androidMain/       # Android 平台代码
│       ├── desktopMain/       # Desktop 平台代码
│       └── iosMain/           # iOS 平台代码
├── gradle/libs.versions.toml  # 依赖版本管理
└── README.md
```

## 🎵 Music 模块

### 已实现

- ✅ 酷我音乐 API 集成
- ✅ 首页推荐歌单（带封面、播放量）
- ✅ 搜索歌曲（正版原曲、专辑封面）
- ✅ Desktop 端运行
- ✅ Android 端运行
- ✅ Landing 首页双入口导航

### 待开发

- [ ] 播放器（播放 / 暂停 / 上下首 / 进度条）
- [ ] 歌词显示
- [ ] 歌单详情页（点击歌单查看歌曲列表）
- [ ] 酷狗音乐源接入
- [ ] 多音源聚合搜索
- [ ] 播放列表管理
- [ ] 本地收藏
- [ ] 下载管理
- [ ] 桌面端歌词悬浮窗
- [ ] iOS 端适配验证

### 音乐源

| 源 | 搜索 | 播放 | 歌词 | 歌单 |
|----|------|------|------|------|
| 酷我音乐 | ✅ | ✅ | 🚧 | ✅ |
| 酷狗音乐 | 🚧 | 🚧 | — | — |

### 可用 API

```bash
# 搜索歌曲（正版原曲）
GET https://search.kuwo.cn/r.s?all={keyword}&ft=music&client=kt&pn=0&rn=20&rformat=json&encoding=utf8&vipver=1&ver=kwplayer_ar_12.2.2.0&strategy=2012&vermerge=1&mobi=1

# 获取播放地址
GET https://antiserver.kuwo.cn/anti.s?type=convert_url&rid=MUSIC_{id}&format=mp3&response=url

# 推荐歌单
GET http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=1&rn=30&order=hot

# 分类歌单
GET http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?id={tagId}&pn=1&rn=30

# 歌单详情
GET http://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pid={pid}&pn=0&rn=100&encode=utf8&keyset=pl2012
```

## 🎨 UI Kit 模块

现代美学组件库，Coming Soon。

## 构建运行

### 前置要求

- JDK 17+
- Android SDK（Android 端）
- Xcode（iOS 端）

### 命令

```bash
# Desktop 端运行
./gradlew :composeApp:run

# Android 端安装（需连接设备或模拟器）
./gradlew :composeApp:installDebug

# 仅编译检查
./gradlew :composeApp:compileKotlinDesktop
```

## License

MIT
