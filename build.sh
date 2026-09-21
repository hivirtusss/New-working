#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
DEPS="$ROOT/deps"
BUILD="$ROOT/build"
DIST="$ROOT/dist"
MODULE="$ROOT/module"
ABI="arm64-v8a"

NDK="${ANDROID_NDK_HOME:-${ANDROID_NDK:-}}"
if [[ -z "$NDK" && -d "$HOME/Android/Sdk/ndk" ]]; then
  NDK="$(ls -d "$HOME/Android/Sdk/ndk"/* 2>/dev/null | sort -V | tail -1)"
fi

echo "==> Fetching dependencies"
mkdir -p "$DEPS/include"

if [[ ! -d "$DEPS/imgui" ]]; then
  git clone --depth 1 https://github.com/ocornut/imgui.git "$DEPS/imgui"
fi

if [[ ! -f "$DEPS/include/zygisk.hpp" ]]; then
  curl -fsSL \
    "https://raw.githubusercontent.com/topjohnwu/zygisk-module-sample/master/module/jni/zygisk.hpp" \
    -o "$DEPS/include/zygisk.hpp"
fi

if [[ -z "$NDK" || ! -d "$NDK" ]]; then
  echo "ERROR: Android NDK not found."
  echo "Set ANDROID_NDK_HOME to your NDK path, then re-run ./build.sh"
  exit 1
fi

echo "==> Building native library (NDK: $NDK)"
rm -rf "$BUILD"
mkdir -p "$BUILD"

cmake -S "$ROOT/native" -B "$BUILD" \
  -DCMAKE_TOOLCHAIN_FILE="$NDK/build/cmake/android.toolchain.cmake" \
  -DANDROID_ABI="$ABI" \
  -DANDROID_PLATFORM=android-26 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build "$BUILD" -j"$(nproc)"

SO_SRC="$(find "$BUILD" -name 'libhivirtus_floating_menu.so' -print -quit)"
if [[ -z "$SO_SRC" || ! -f "$SO_SRC" ]]; then
  echo "ERROR: libhivirtus_floating_menu.so not found in $BUILD"
  exit 1
fi

echo "==> Packing Magisk module ZIP"
rm -rf "$DIST"
mkdir -p "$DIST/zygisk"

cp -r "$MODULE/META-INF" "$DIST/"
cp "$MODULE/module.prop" "$DIST/"
cp "$MODULE/customize.sh" "$DIST/"
cp "$SO_SRC" "$DIST/zygisk/${ABI}.so"

ZIP_NAME="HivirtusFloatingMenu-$(grep '^version=' "$MODULE/module.prop" | cut -d= -f2).zip"
(cd "$DIST" && zip -r9 "$ROOT/$ZIP_NAME" .)

echo ""
echo "Done: $ROOT/$ZIP_NAME"
echo "Flash via Magisk / KernelSU (Zygisk enabled)"
