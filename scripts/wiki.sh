#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
wiki_directory_path="$repository_root_path/wiki"

cd "$wiki_directory_path"

if ! command -v docker >/dev/null 2>&1; then
  printf '%s\n' 'Docker was not found. Install Docker Desktop first, then rerun this script.'
  exit 1
fi

if [ "${1:-}" = "--detached" ]; then
  printf '%s\n' 'Starting the Mosmena local wiki in detached mode...'
  docker compose up -d
else
  printf '%s\n' 'Starting the Mosmena local wiki...'
  docker compose up
fi
