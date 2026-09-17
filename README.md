# ALLINONE 🎵

Compose Multiplatform 聚合音乐平台，一个 App 听遍全网音乐。

## 平台支持

- 🤖 Android
- 🍎 iOS
- 🖥️ Desktop (macOS / Windows / Linux)

## 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin Multiplatform | 跨平台共享逻辑 |
| Compose Multiplatform | 跨平台 UI |
| Ktor | 网络请求 |
| Kotlinx Serialization | JSON 解析 |
| Coil 3 | 图片加载 |
| Voyager | 导航 |
| Koin | 依赖注入 |

## 音乐源

| 源 | 搜索 | 播放 | 歌词 | 歌单 |
|----|------|------|------|------|
| 酷我音乐 | ✅ | ✅ | ✅ | ✅ |
| 酷狗音乐 | 🚧 | 🚧 | 🚧 | - |

## 功能规划

- [x] 项目初始化
- [x] 酷我音乐 API 集成（搜索、播放地址、歌单推荐）
- [x] 首页推荐歌单（带封面、播放量）
- [x] 搜索歌曲（带专辑封面、正版原曲）
- [x] Desktop 端运行
- [x] Android 端运行
- [ ] 播放器（播放 / 暂停 / 上下首 / 进度条）
- [ ] 歌词显示
- [ ] 歌单详情页
- [ ] 多音源聚合搜索（酷狗）
- [ ] 播放列表管理
- [ ] 本地收藏
- [ ] 下载管理
- [ ] 桌面端歌词悬浮窗
- [ ] iOS 端适配

## 已验证可用的 API

```
# 搜索歌曲
GET http://search.kuwo.cn/r.s?all={keyword}&ft=music&rformat=json&encoding=utf8

# 获取播放地址
GET https://antiserver.kuwo.cn/anti.s?type=convert_url&rid=MUSIC_{id}&format=mp3&response=url

# 推荐歌单
GET http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=1&rn=30&order=hot

# 分类歌单
GET http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?id={tagId}&pn=1&rn=30

# 歌单详情
GET http://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pid={pid}&pn=0&rn=100&encode=utf8
```

## 构建运行

```bash
# Desktop
./gradlew :composeApp:run

# Android
./gradlew :composeApp:assembleDebug

# iOS
# 用 Xcode 打开 iosApp/iosApp.xcodeproj
```

## License

MIT
