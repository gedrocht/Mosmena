#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

gradle_arguments="detekt ktlintCheck lintDebug testDebugUnitTest koverVerify :app:dokkaGeneratePublicationHtml"

while [ $# -gt 0 ]; do
  case "$1" in
    --clean)
      gradle_arguments="clean $gradle_arguments"
      ;;
    --include-instrumentation-tests)
      gradle_arguments="$gradle_arguments connectedDebugAndroidTest"
      ;;
    *)
      printf 'Unknown option: %s\n' "$1"
      exit 1
      ;;
  esac
  shift
done

echo "Running Mosmena quality suite with Gradle tasks: $gradle_arguments"
./gradlew $gradle_arguments
