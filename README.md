# Mosmena

This repository has been bootstrapped with a strict GitHub quality and security baseline.

## What is included

- Git repository initialization
- Repository hygiene defaults via `.editorconfig`, `.gitattributes`, and `.gitignore`
- Pull request guidance and `CODEOWNERS`
- Dependabot for GitHub Actions and common package ecosystems
- GitHub Actions workflows for:
  - repository policy checks
  - workflow linting
  - broad repo linting
  - dependency review on pull requests
  - secret scanning
  - CodeQL static analysis
  - project test execution with coverage upload when supported

## Project test integration

The CI workflow is intentionally language-aware and auto-detects common project manifests:

- `package.json`: runs `npm ci` and `npm test -- --coverage` when available
- `pyproject.toml` or `requirements*.txt`: creates a virtual environment and runs `pytest` with coverage if configured

If your project uses another stack, extend `.github/workflows/ci.yml` with the relevant install and test commands.

## Recommended next steps

1. Add your application source code.
2. Add project-native linters and tests.
3. Enable branch protection on GitHub so these checks are required before merge.
