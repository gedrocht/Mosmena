#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

if ! command -v adb >/dev/null 2>&1; then
  printf '%s\n' 'adb was not found. Install Android platform-tools or open Android Studio and install them first.'
  exit 1
fi

if ! adb devices | grep -q 'device$'; then
  printf '%s\n' 'No Android device or emulator is connected. Start an emulator in Android Studio or connect a physical device with USB debugging enabled.'
  exit 1
fi

printf '%s\n' 'Installing the debug build on the connected Android device or emulator...'
./gradlew installDebug

printf '%s\n' 'Launching Mosmena...'
adb shell am start -n com.gedrocht.mosmena/.ui.MainActivity
