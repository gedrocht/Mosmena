#!/usr/bin/env sh

set -eu

get_command_path_if_available() {
  command -v "$1" 2>/dev/null || true
}

get_android_sdk_path_if_available() {
  for candidate_android_sdk_path in \
    "${ANDROID_SDK_ROOT:-}" \
    "${ANDROID_HOME:-}" \
    "$HOME/Android/Sdk"
  do
    if [ -n "$candidate_android_sdk_path" ] && [ -d "$candidate_android_sdk_path" ]; then
      printf '%s\n' "$candidate_android_sdk_path"
      return 0
    fi
  done

  return 1
}

write_check_result() {
  label="$1"
  passed="$2"
  details="$3"

  if [ "$passed" = "true" ]; then
    status_text="OK"
  else
    status_text="MISSING"
  fi

  printf '[%s] %s - %s\n' "$status_text" "$label" "$details"
}

java_command_path=$(get_command_path_if_available java)
python_command_path=$(get_command_path_if_available python3)
docker_command_path=$(get_command_path_if_available docker)
adb_command_path=$(get_command_path_if_available adb)
android_sdk_path=""

if android_sdk_path=$(get_android_sdk_path_if_available); then
  :
else
  android_sdk_path=""
fi

if [ -z "$adb_command_path" ] && [ -n "$android_sdk_path" ] && [ -x "$android_sdk_path/platform-tools/adb" ]; then
  adb_command_path="$android_sdk_path/platform-tools/adb"
fi

printf 'Mosmena prerequisite check\n'
printf '===========================\n\n'

if [ -n "$java_command_path" ]; then
  write_check_result "Java 17 or newer" "true" "$java_command_path"
else
  write_check_result "Java 17 or newer" "false" "Install Android Studio or a JDK 17 distribution."
fi

if [ -n "$android_sdk_path" ]; then
  write_check_result "Android SDK" "true" "$android_sdk_path"
else
  write_check_result "Android SDK" "false" "Install Android Studio, then install Android SDK platform 36 and build-tools 36.0.0."
fi

if [ -n "$adb_command_path" ]; then
  write_check_result "adb" "true" "$adb_command_path"
else
  write_check_result "adb" "false" "Install Android platform-tools or let Android Studio install them."
fi

if [ -n "$python_command_path" ]; then
  write_check_result "Python" "true" "$python_command_path"
else
  write_check_result "Python" "false" "Install Python 3 if you want to build or serve the docs site."
fi

if [ -n "$docker_command_path" ]; then
  write_check_result "Docker" "true" "$docker_command_path"
else
  write_check_result "Docker" "false" "Install Docker Desktop if you want to run the local DokuWiki."
fi

printf '\nSuggested next commands\n'
printf '%s\n' '-----------------------'
printf '%s\n' './scripts/setup.sh'
printf '%s\n' './scripts/build.sh'
printf '%s\n' './scripts/run-android.sh'
printf '%s\n' './scripts/test.sh'
printf '%s\n' './scripts/docs.sh --serve'
printf '%s\n' './scripts/wiki.sh --detached'
