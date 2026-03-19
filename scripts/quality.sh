#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

gradle_arguments="clean detekt ktlintCheck lintDebug testDebugUnitTest koverVerify :app:dokkaGeneratePublicationHtml"

if [ "${1:-}" = "--include-instrumentation-tests" ]; then
  gradle_arguments="$gradle_arguments connectedDebugAndroidTest"
fi

echo "Running Mosmena quality suite with Gradle tasks: $gradle_arguments"
./gradlew $gradle_arguments
