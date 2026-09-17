# 踩坑记录 🕳️

开发 ALLINONE 过程中遇到的问题和解决方案。

---

## 1. 酷我搜索 API 返回的不是标准 JSON

**现象**：`search.kuwo.cn/r.s` 返回的响应使用**单引号和双引号混合**的伪 JSON 格式，直接用 JSON 解析器会报错。

**示例**：
```
{'SONGNAME':'晴天','ARTIST':'周杰伦',"web_albumpic_short":"120/s3s94/93/xxx.jpg"}
```
大部分字段用单引号，但 `web_albumpic_short` 等字段用双引号。

**踩坑过程**：
1. 最初用 `kotlinx.serialization` 的 `isLenient = true` 解析，发现它能解析单引号 JSON，但提取出来的混合引号字段值为空
2. 尝试 naive 的 `replace("'", "\"")` 替换，但空值 `''` 会变成 `""` 破坏结构，且已有双引号部分会被打乱
3. 最终方案：**状态机遍历**，遇到 `"` 开头的部分原样保留到闭合 `"`，遇到 `'` 则替换为 `"`

**解决代码**：
```kotlin
private fun fixKuwoJson(text: String): String {
    val raw = text.trim()
    val sb = StringBuilder(raw.length)
    var i = 0
    while (i < raw.length) {
        val c = raw[i]
        if (c == '\'') {
            sb.append('"')
            i++
        } else if (c == '"') {
            sb.append(c); i++
            while (i < raw.length && raw[i] != '"') { sb.append(raw[i]); i++ }
            if (i < raw.length) { sb.append(raw[i]); i++ }
        } else {
            sb.append(c); i++
        }
    }
    return sb.toString().replace("&nbsp;", " ")
}
```

---

## 2. 酷我搜索结果质量差（翻唱/片段居多）

**现象**：用基础参数搜索"周杰伦"，返回的大部分是翻唱、DJ 版、片段、伴奏，几乎没有正版原曲。

**原因**：`search.kuwo.cn/r.s` 的基础参数 `itemset=web_2013` 返回的是网页端低质量结果。

**解决**：添加酷我 Android 客户端参数，模拟正版客户端搜索：
```
vipver=1
ver=kwplayer_ar_12.2.2.0
strategy=2012
vermerge=1
mobi=1
issubtitle=1
cluster=0
```

加上这些参数后，搜索"周杰伦"返回的第一条就是《晴天》专辑版。

---

## 3. 专辑封面 URL 拼接方式已失效

**现象**：按 ALBUMID 拼接 `https://img4.kuwo.cn/star/albumcover/300/{albumId}.jpg` 返回 404。

**解决**：使用 API 返回的 `web_albumpic_short` 字段，但要注意：

- 字段值格式：`120/s3s94/93/211513640.jpg`，其中 `120` 是尺寸前缀
- 正确的 URL 拼法：`https://img2.kuwo.cn/star/albumcover/500/{去掉120/后的路径}`
- 即：`albumPicShort.substringAfter("/")` 去掉尺寸前缀

```
❌ https://img4.kuwo.cn/star/albumcover/300/1293.jpg          → 404
❌ https://img2.kuwo.cn/star/albumcover/500/120/s3s94/93/xxx.jpg → 404
✅ https://img2.kuwo.cn/star/albumcover/500/s3s94/93/xxx.jpg     → 200
```

---

## 4. Coil 3 在 KMP 项目中不加载网络图片

**现象**：`AsyncImage` 组件不显示任何图片，没有报错，也没有网络请求。

**原因**：Coil 3 的 KMP 版本默认**没有网络加载能力**，必须显式注册 `KtorNetworkFetcherFactory`。

**解决**：在 `App()` composable 中配置 ImageLoader：
```kotlin
setSingletonImageLoaderFactory { context ->
    ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
        }
        .crossfade(true)
        .build()
}
```

需要依赖 `coil-network-ktor3`：
```toml
coil-network-ktor = { module = "io.coil-kt.coil3:coil-network-ktor3", version.ref = "coil" }
```

---

## 5. Android 端推荐页加载不出数据

**现象**：Desktop 端正常加载推荐歌单，Android 端进入推荐页永远显示加载中或加载失败。

**原因**：酷我推荐歌单 API 是 `http://wapi.kuwo.cn`（HTTP 明文），Android 9+ 默认禁止明文 HTTP 请求（`CLEARTEXT_NOT_PERMITTED`）。

**解决**：添加网络安全配置文件：

`res/xml/network_security_config.xml`：
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

`AndroidManifest.xml` 中引用：
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## 6. Android 端缺少 activity-compose 依赖

**现象**：Desktop 编译通过，Android 编译报错 `Unresolved reference 'ComponentActivity'`。

**原因**：`ComponentActivity`、`setContent`、`enableEdgeToEdge` 来自 `androidx.activity:activity-compose`，CMP 模板没有自动包含。

**解决**：在 `composeApp/build.gradle.kts` 的 `androidMain` 依赖中添加：
```kotlin
androidMain.dependencies {
    implementation("androidx.activity:activity-compose:1.9.3")
}
```

---

## 7. Android 缺少启动图标导致构建失败

**现象**：`processDebugResources` 失败，报错 `resource mipmap/ic_launcher not found`。

**原因**：`AndroidManifest.xml` 中引用了 `@mipmap/ic_launcher`，但项目中没有对应资源文件。

**解决**：创建最小化的自适应图标：
- `res/mipmap-anydpi-v26/ic_launcher.xml` — adaptive-icon 使用纯色
- `res/mipmap-hdpi/ic_launcher.png` — 兜底 PNG
- `res/values/colors.xml` — 定义图标颜色

---

## 8. 酷我推荐歌单 API (bd-api.kuwo.cn) 已失效

**现象**：`https://bd-api.kuwo.cn/api/v1/playlist/recommend` 返回 404，`bd-api.kuwo.cn` 下的歌单相关接口全部失效。

**原因**：波点音乐（Bodian）的 API 域名已下线。

**解决**：切换到酷我 PC 端开放 API：
```
推荐歌单：http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList
分类歌单：http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList
歌单详情：http://nplserver.kuwo.cn/pl.svc?op=getlistinfo
```

注意：`wapi.kuwo.cn` 的接口需要带 `Referer: http://www.kuwo.cn/` 头。

---

## 9. Gradle 国内构建缓慢

**现象**：首次构建下载依赖需要 10+ 分钟，经常超时。

**解决**：在 `settings.gradle.kts` 中添加阿里云镜像，放在 Google/Maven Central 前面：
```kotlin
repositories {
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/public")
    google()
    mavenCentral()
}
```

---

## 总结

| 问题 | 根因 | 一句话解决 |
|------|------|-----------|
| JSON 解析失败 | 酷我用单引号+双引号混合 | 状态机转换，双引号段原样保留 |
| 搜索质量差 | 缺少客户端参数 | 加 `vipver=1&ver=kwplayer_ar_12.2.2.0` |
| 封面 404 | URL 拼接规则变了 | 用 `web_albumpic_short` 去掉尺寸前缀 |
| Coil 不加载图片 | KMP 默认无网络引擎 | 注册 `KtorNetworkFetcherFactory` |
| Android 无网络 | HTTP 明文被拦截 | `networkSecurityConfig` 允许 cleartext |
| Android 编译失败 | 缺 activity-compose | 添加依赖 |
| 推荐 API 404 | bd-api.kuwo.cn 下线 | 换 wapi.kuwo.cn |
