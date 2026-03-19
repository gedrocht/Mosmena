#!/usr/bin/env sh

set -eu

printf 'Mosmena command guide\n'
printf '=====================\n\n'
printf '%s\n' '1. Check prerequisites'
printf '%s\n\n' './scripts/doctor.sh'
printf '%s\n' '2. Prepare local configuration'
printf '%s\n\n' './scripts/setup.sh'
printf '%s\n' '3. Build the Android app'
printf '%s\n\n' './scripts/build.sh'
printf '%s\n' '4. Run the app on a connected device or emulator'
printf '%s\n\n' './scripts/run-android.sh'
printf '%s\n' '5. Run unit tests'
printf '%s\n\n' './scripts/test.sh'
printf '%s\n' '6. Run the main quality suite'
printf '%s\n\n' './scripts/quality.sh'
printf '%s\n' '7. Build or serve docs'
printf '%s\n' './scripts/docs.sh'
printf '%s\n\n' './scripts/docs.sh --serve'
printf '%s\n' '8. Run the local wiki'
printf '%s\n' './scripts/wiki.sh --detached'
