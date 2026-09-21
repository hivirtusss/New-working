# Hivirtus Menu (KC GL logic)

KC GL style floating menu APK — **no login, no key**, personal use.

## KC GL logic ported

| KC GL | Hivirtus Menu |
|-------|---------------|
| Version select (Lite/Std/Pro) | `LauncherActivity` |
| Root + assets + overlay deploy | `DeployActivity` |
| Personal dashboard | `DashboardActivity` |
| Settings + game server | `SettingsActivity` |
| `SuperJNI.SaveMenuIni` | `KernelBridge.saveMenuIni` |
| `SuperJNI.getPID` | `KernelBridge.getPid` + overlay service |
| `StartGame` launch flow | `StartGameController` |
| `recoil_data_kcgl.ini` | Same path |
| Floating KC bubble menu | `FloatingMenuService` |

**Not ported:** `libkernel.so` game cheat hooks (ESP/aim injection).

## App flow

```
Launcher (version) → Deploy (root/assets/overlay) → Dashboard → Start Game
```

## Build APK

```bash
cd android-app
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

Requirements: Android Studio / SDK 34, JDK 17, rooted phone for full flow.

## Config

`/data/local/tmp/recoil_data_kcgl.ini`

## Zygisk module (optional)

```bash
export ANDROID_NDK_HOME=/path/to/ndk
./build.sh
```
