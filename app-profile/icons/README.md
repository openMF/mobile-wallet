# Fork app icons

Drop platform-specific app-icon files here, then run:

```bash
./gradlew syncForkConfig
```

`syncForkConfig` copies whatever it finds in this directory to the canonical platform locations. **Files are optional**: any name missing is skipped and the template default at the destination is preserved — so an empty `app-profile/icons/` is a valid state (you get the openMF defaults).

## File contract

| Drop here                              | Copied to                                                                    | Notes                                                          |
|----------------------------------------|------------------------------------------------------------------------------|----------------------------------------------------------------|
| `ios.png`                              | `cmp-ios/iosApp/Assets.xcassets/AppIcon.appiconset/AppIcon.png`              | 1024×1024 PNG, single-size universal (Xcode 14+ format)        |
| `web-favicon.ico`                      | `cmp-web/src/jsMain/resources/favicon.ico` AND `…/wasmJsMain/…/favicon.ico` | Multi-resolution ICO (typically 16+32+48 sizes)                |
| `desktop-macos.icns`                   | `cmp-desktop/icons/ic_launcher.icns`                                         | macOS `.icns` bundle                                           |
| `desktop-windows.ico`                  | `cmp-desktop/icons/ic_launcher.ico`                                          | Windows `.ico` (multi-res)                                     |
| `desktop-linux.png`                    | `cmp-desktop/icons/ic_launcher.png`                                          | 512×512 PNG                                                    |
| `android/` (optional)                  | `cmp-android/src/main/res/` (tree-copy, mirrors subdirs)                     | See Android section below                                      |

## Generating each format

- **iOS** (`ios.png`) — Single 1024×1024 PNG. Modern Xcode (14+) auto-resizes at archive time, so one source file covers every iOS device + the App Store listing. Export from your design tool at 1024×1024, no transparency, no rounded corners (Apple's mask rounds it for you).

- **Web favicon** (`web-favicon.ico`) — Multi-resolution ICO. Easiest: <https://favicon.io/favicon-converter/> (drop a 512×512 PNG, get a single ICO with 16/32/48 sizes packed in).

- **macOS** (`desktop-macos.icns`) — From a 1024×1024 PNG:
  ```bash
  mkdir icon.iconset
  for size in 16 32 64 128 256 512 1024; do
    sips -z $size $size source.png --out icon.iconset/icon_${size}x${size}.png
    sips -z $((size*2)) $((size*2)) source.png --out icon.iconset/icon_${size}x${size}@2x.png
  done
  iconutil -c icns icon.iconset -o desktop-macos.icns
  ```

- **Windows** (`desktop-windows.ico`) — Multi-resolution ICO with 16/24/32/48/64/256 sizes. Use <https://convertio.co/png-ico/> or `magick convert source-512.png -define icon:auto-resize=256,64,48,32,24,16 desktop-windows.ico` (ImageMagick).

- **Linux** (`desktop-linux.png`) — A single 512×512 PNG.

## Android adaptive icons

Android icons are **two-layer adaptive** (Android 8+) with WEBP fallbacks for older devices. The full tree consists of:

```
cmp-android/src/main/res/
  drawable/ic_launcher_foreground.xml      ← vector, your logo
  drawable/ic_launcher_background.xml      ← vector or color
  mipmap-anydpi-v26/ic_launcher.xml        ← adaptive-icon ref
  mipmap-anydpi-v26/ic_launcher_round.xml  ← adaptive-icon ref (round)
  mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.webp        ← 5× density
  mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher_round.webp  ← 5× density (round)
  values/ic_launcher_background.xml         ← background color resource
```

**Phase 1 (today):** Generate this tree once with Android Studio's **Image Asset Studio**:
1. File → New → Image Asset
2. Icon Type: "Launcher Icons (Adaptive and Legacy)"
3. Drop your foreground/background sources
4. Studio writes the full tree directly into `cmp-android/src/main/res/`
5. Commit the result

Alternatively, drop a pre-built `res/` tree into `app-profile/icons/android/` (mirroring the directory structure shown above), and `syncForkConfig` will copy it into `cmp-android/src/main/res/`.

**Phase 2 (planned):** `./gradlew syncForkConfig --regenerate-android` — single source PNG → all 12 files, using ImageMagick if available. Tracking issue: TBD.

## Why this exists

The template's `syncForkConfig` task already propagates *text* fork-identity fields (`appId`, `appDisplayName`, `iosTeamId`) from `gradle/libs.versions.toml` to platform config files. Icons are binary, so the contract is:

- **Source of truth:** files in `app-profile/icons/`
- **Generated targets:** the canonical platform locations listed above
- **Idempotent:** safe to re-run; missing source = no-op
- **Per-fork visibility:** `git status` after `syncForkConfig` shows exactly which platform assets the fork is overriding

See `build-logic/convention/src/main/kotlin/SyncForkConfigPlugin.kt` for the task implementation.
