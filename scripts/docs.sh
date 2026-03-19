#!/usr/bin/env sh

set -eu

repository_root_path=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
documentation_virtual_environment_path="$repository_root_path/.venv-docs"
documentation_virtual_environment_python_path="$documentation_virtual_environment_path/bin/python"

cd "$repository_root_path"

if ! command -v python3 >/dev/null 2>&1; then
  printf '%s\n' 'Python 3 was not found. Install Python 3 first, then rerun this script.'
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  printf '%s\n' 'Java 17 was not found. Install Java Development Kit 17 first, then rerun this script.'
  exit 1
fi

if [ ! -x "$documentation_virtual_environment_python_path" ]; then
  printf '%s\n' 'Creating a Python virtual environment for documentation tools...'
  python3 -m venv "$documentation_virtual_environment_path"
fi

printf '%s\n' 'Installing MkDocs dependencies into .venv-docs...'
"$documentation_virtual_environment_python_path" -m pip install --upgrade pip
"$documentation_virtual_environment_python_path" -m pip install mkdocs==1.6.1 mkdocs-material==9.5.34

printf '%s\n' 'Generating API reference with Dokka...'
./gradlew :app:dokkaGeneratePublicationHtml

if [ "${1:-}" = "--serve" ]; then
  printf '%s\n' 'Serving documentation at http://127.0.0.1:8000'
  "$documentation_virtual_environment_python_path" -m mkdocs serve
else
  printf '%s\n' 'Building documentation into the site directory...'
  "$documentation_virtual_environment_python_path" -m mkdocs build --strict
fi
