# Hivirtus Floating Menu

Zygisk floating menu mod — **sirf ZIP flash**, koi alag APK install nahi.

## Features

- Floating **HM** button game ke upar (ImGui overlay)
- Tap se menu open/close
- Draggable position
- Local config file (no server)
- Target: BGMI (`com.pubg.imobile`) aur PUBG Global (`com.tencent.ig`)

## Requirements

- Rooted device with **Magisk** (Zygisk ON) ya **KernelSU** + Zygisk
- Android arm64
- Android NDK (build ke liye)

## Build

```bash
export ANDROID_NDK_HOME=/path/to/ndk
chmod +x build.sh
./build.sh
```

Output: `HivirtusFloatingMenu-v1.0.0.zip`

## Install

1. ZIP ko Magisk / KernelSU se flash karo
2. Reboot
3. Game open karo — top-left area mein **HM** button dikhega

## Config (local)

File: `/data/local/tmp/hivirtus_menu.conf`

```ini
menu_x=100
menu_y=200
menu_open=0
toggle_esp=0
toggle_aim=0
```

Values edit karke game restart karo, ya menu se toggle karo (auto-save next version).

## Project structure

```
module/          Magisk flashable files
native/          Zygisk C++ module (ImGui + EGL hook)
build.sh         NDK build + ZIP pack
```

## Target package change

`native/main.cpp` mein `TARGET_PACKAGES` array edit karo.

## Notes

- Yeh v1 skeleton hai — ESP/Aim toggles abhi placeholder hain
- Touch input game engine pe depend karta hai; agar button respond na kare to input hook add karna padega
- Sirf educational / apne device testing ke liye — online games mein use ToS violate kar sakta hai

## Rules (confirmed)

- Sirf **ZIP flash** — koi alag APK install nahi
- **Floating window** module ke andar se
- Local config only
