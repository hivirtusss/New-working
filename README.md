# Hivirtus Floating Menu

KC GL style floating menu project — **UI + overlay shell** (no game cheat hooks).

## KC GL reference analysis

Downloaded `KC GL.apk` (`com.android.lc`, ~26MB) structure:

| Part | Details |
|------|---------|
| Flow | Login → Root/overlay setup → Dashboard → Launch game |
| Overlay | `org.exploit.depth` + `SYSTEM_ALERT_WINDOW` |
| Native | `libkernel.so` (ESP/aim/recoil — **not included here**) |
| Config | `/data/local/tmp/recoil_data_kcgl.ini` |
| Games | Global, Korea, TW, VNG, India (BGMI) |

This repo recreates the **app flow + floating bubble menu UI**. Toggles save to the same ini path. Native kernel injection / ESP rendering is intentionally **not implemented**.

## Android app (recommended — like KC GL)

```bash
cd android-app
# Android Studio se open karo, ya:
./gradlew assembleDebug
```

### App flow

1. **Login** — local username/password save
2. **Setup** — root + overlay permission
3. **Home** — Start game + overlay service
4. **Settings** — game server, Hide ESP, touch, gyro toggles
5. **Floating menu** — draggable `KC` bubble + panel (Visual / Aim sections)

### Config file

`/data/local/tmp/recoil_data_kcgl.ini`

## Zygisk module (alternative — ZIP flash)

Previous skeleton still available:

```bash
export ANDROID_NDK_HOME=/path/to/ndk
./build.sh
```

## Important

- Educational / UI development only
- Online games mein cheat use ToS violate karta hai
- KC GL jaisa full native cheat clone yahan nahi hai — sirf structure + overlay UI
