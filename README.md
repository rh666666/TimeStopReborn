# Time Stop: Reborn

An unofficial continuation of [MegaTimeStop](https://github.com/Mega32K/MegaTimeStop), ported to Minecraft 1.21.1 on NeoForge.

The project is split into `core/` (time-stop engine and API) and `mod/` (default items and content). At runtime they are merged into a single mod JAR.

[中文说明](README-zh_cn.md)

## Building

Requires JDK 21.

```powershell
.\gradlew build
```

Output is under `build/libs/` (full mod JAR); the Lib build is at `core/build/libs/timestop-core-<version>.jar`.

## Library dependency

Maven coordinates: `com.adoleiiiiii.timestop:timestop-core:<version>`

```powershell
.\gradlew :core:publishToMavenLocal
```

```gradle
dependencies {
    compileOnly "com.adoleiiiiii.timestop:timestop-core:1.0.0"
    localRuntime "com.adoleiiiiii.timestop:timestop-core:1.0.0"
}
```

## Credits

- **Mega32K** — original [MegaTimeStop](https://github.com/Mega32K/MegaTimeStop) mod

## License

[GPL-3.0-only](LICENSE)
