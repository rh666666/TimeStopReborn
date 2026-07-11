# 时停：重生

[MegaTimeStop](https://github.com/Mega32K/MegaTimeStop) 的非官方延续版，基于 Minecraft 1.21.1 与 NeoForge 移植。

项目分为 `core/`（时停核心与 API）与 `mod/`（怀表、飞刀等默认内容），运行时合并为单个模组 JAR。

[English README](README.md)

## 构建

需要 JDK 21。

```powershell
.\gradlew build
```

产物位于 `build/libs/`（完整模组 JAR）；Lib 精简版位于 `core/build/libs/timestop-core-<version>.jar`。

## Lib 依赖

Maven 坐标：`com.adoleiiiiii.timestop:timestop-core:<version>`

```powershell
.\gradlew :core:publishToMavenLocal
```

```gradle
dependencies {
    compileOnly "com.adoleiiiiii.timestop:timestop-core:1.0.0"
    localRuntime "com.adoleiiiiii.timestop:timestop-core:1.0.0"
}
```

## 致谢

- **Mega32K** — 原版 [MegaTimeStop](https://github.com/Mega32K/MegaTimeStop) 模组

## 许可证

[GPL-3.0-only](LICENSE)
