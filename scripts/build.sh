#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

gradle_arguments="--no-configuration-cache -x lintVitalAnalyzeRelease -x lintVitalReportRelease -x lintVitalRelease "
if [ "${1:-}" = "--clean" ]; then
  gradle_arguments="${gradle_arguments}clean "
fi

echo "Building Mosmena with Gradle tasks: ${gradle_arguments}assembleDebug assembleRelease"
./gradlew ${gradle_arguments}assembleDebug assembleRelease
