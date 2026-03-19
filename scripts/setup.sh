#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

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

printf 'Mosmena beginner setup\n'
printf '======================\n\n'
printf '%s\n' 'Step 1: Install Android Studio if you do not already have it.'
printf '%s\n' 'Step 2: In Android Studio, install Android SDK platform 36, build-tools 36.0.0, and platform-tools.'
printf '%s\n' 'Step 3: Install Python 3 if you want to build or serve the docs site.'
printf '%s\n' 'Step 4: Install Docker Desktop if you want to run the local wiki.'
printf '\n'

if android_sdk_path=$(get_android_sdk_path_if_available); then
  if [ ! -f local.properties ]; then
    printf 'sdk.dir=%s\n' "$android_sdk_path" > local.properties
    printf 'Created local.properties pointing at:\n%s\n\n' "$android_sdk_path"
  else
    printf '%s\n\n' 'local.properties already exists.'
  fi
else
  printf '%s\n\n' 'No Android SDK path was detected automatically, so local.properties was not created.'
fi

printf '%s\n' 'Next commands'
printf '%s\n' '-------------'
printf '%s\n' './scripts/doctor.sh'
printf '%s\n' './scripts/build.sh'
printf '%s\n' './scripts/run-android.sh'
printf '%s\n' './scripts/test.sh'
