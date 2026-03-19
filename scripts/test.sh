#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$repository_root_path"

echo "Running Mosmena unit tests and coverage verification"
./gradlew testDebugUnitTest koverXmlReport koverVerify
