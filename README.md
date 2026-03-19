# Mosmena

[![CI](https://github.com/gedrocht/Mosmena/actions/workflows/ci.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/ci.yml)
[![Security](https://github.com/gedrocht/Mosmena/actions/workflows/security.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/security.yml)
[![Docs](https://github.com/gedrocht/Mosmena/actions/workflows/pages.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/pages.yml)
[![GitHub Pages](https://img.shields.io/badge/docs-GitHub%20Pages-0A7EA4)](https://gedrocht.github.io/Mosmena/)
[![License: MIT](https://img.shields.io/badge/license-MIT-2ea44f.svg)](LICENSE)

Mosmena is an Android acoustic ranging demo and learning project. It emits a short near-ultrasonic pulse from the phone speaker, records the response through the microphone, estimates the nearest reflection, and visualizes the result while keeping the code and docs beginner-friendly.

GitHub description:
Android acoustic ranging demo that emits near-ultrasonic pulses, estimates the nearest reflection, and teaches the codebase through beginner-first docs.

Quick links:
- Docs site: [gedrocht.github.io/Mosmena](https://gedrocht.github.io/Mosmena/)
- Beginner quickstart: [docs/beginner-quickstart.md](docs/beginner-quickstart.md)
- Script guide: [scripts/README.md](scripts/README.md)
- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)

## Start Here If You Are New

If you are a complete beginner, use this exact path:

1. Read [scripts/README.md](scripts/README.md).
2. Run `.\scripts\doctor.ps1` on Windows or `./scripts/doctor.sh` on macOS/Linux.
3. Run `.\scripts\setup.ps1` or `./scripts/setup.sh`.
4. Build the app with `.\scripts\build.ps1` or `./scripts/build.sh`.
5. Run the app on a connected device or emulator with `.\scripts\run-android.ps1` or `./scripts/run-android.sh`.
6. Run tests with `.\scripts\test.ps1` or `./scripts/test.sh`.
7. Run the full quality suite with `.\scripts\quality.ps1` or `./scripts/quality.sh`.
8. Build or serve the docs with `.\scripts\docs.ps1 -Serve` or `./scripts/docs.sh --serve`.
9. Start the local wiki with `.\scripts\wiki.ps1 -Detached` or `./scripts/wiki.sh --detached`.

If you just want a command list, run `.\scripts\help.ps1` or `./scripts/help.sh`.

## At a glance

- Platform: Android
- Language: Kotlin
- Main idea: pulse-echo acoustic distance estimation
- Local entrypoints: beginner-friendly scripts under `scripts/`
- Documentation: MkDocs site, Dokka API reference, and a local DokuWiki layer
- Quality gates: lint, Detekt, ktlint, unit tests, instrumentation tests, coverage, and security scans

## Why this repository exists

This repository is intentionally opinionated:

- The Android app is written to be easy to read.
- Variable names are explicit and intentionally non-abbreviated.
- Public code is documented with KDoc so Dokka can publish API documentation.
- Beginner-first docs are published through GitHub Pages.
- A separate DokuWiki layer is included for people who want a slower, tutorial-heavy learning path.
- GitHub Actions enforce strict quality, testing, security, and documentation expectations.

## Important limitations

Phone audio hardware was not designed to be a precision ultrasonic rangefinder. Results depend heavily on:

- speaker and microphone frequency response
- device-specific audio processing
- room layout and surface material
- background noise
- the phone's ability to emit energy above or near 20 kHz

Treat the app as an educational ranging experiment, not a safety-critical measuring tool.

## Repository structure

- `app/`: Android application source, resources, and tests
- `config/`: static analysis configuration
- `docs/`: GitHub Pages documentation source
- `wiki/`: DokuWiki content and Docker Compose configuration
- `scripts/`: local developer entrypoints for build, test, quality, CI, and GitHub helpers
- `scripts/ci/`: repository validation scripts
- `scripts/github/`: optional GitHub repository configuration helpers

## Local development

### Requirements

- Java Development Kit 17
- Android SDK platform 36
- Android build-tools 36.0.0
- Android platform-tools
- Python 3 for the docs site
- Docker Desktop for the local wiki

### Common commands

```bash
./gradlew lintDebug
./gradlew detekt ktlintCheck
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
./gradlew koverXmlReport koverVerify
./gradlew :app:dokkaGeneratePublicationHtml
```

### Local helper scripts

If you prefer discoverable entrypoints under `scripts/`, use:

```bash
./scripts/help.sh
./scripts/doctor.sh
./scripts/setup.sh
./scripts/build.sh
./scripts/run-android.sh
./scripts/test.sh
./scripts/quality.sh
./scripts/docs.sh --serve
./scripts/wiki.sh --detached
```

On Windows PowerShell:

```powershell
.\scripts\help.ps1
.\scripts\doctor.ps1
.\scripts\setup.ps1
.\scripts\build.ps1
.\scripts\run-android.ps1
.\scripts\test.ps1
.\scripts\quality.ps1
.\scripts\docs.ps1 -Serve
.\scripts\wiki.ps1 -Detached
```

Optional flags:

- `build`: `--clean`
- `quality`: `--include-instrumentation-tests`
- `docs`: `--serve`
- `wiki`: `--detached`

## Documentation layers

- Beginner docs site: `docs/` built with MkDocs Material and deployed to GitHub Pages
- API reference: generated from KDoc with Dokka
- Local wiki: `wiki/` powered by DokuWiki for long-form tutorial reading

## External libraries and tools

- [Timber](https://github.com/JakeWharton/timber) for developer-friendly logging
- [Detekt](https://detekt.dev/) for Kotlin static analysis
- [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle) for formatting enforcement
- [Kover](https://kotlin.github.io/kotlinx-kover/gradle-plugin/) for coverage reporting
- [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) for API documentation
- [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) for GitHub Pages documentation
- [DokuWiki](https://www.dokuwiki.org/dokuwiki) for the separate tutorial-heavy wiki layer

Full usage notes are in [docs/external-libraries.md](docs/external-libraries.md).

## GitHub enforcement

The repository includes GitHub Actions for:

- repository policy validation
- Android lint, Detekt, and ktlint
- unit tests and instrumentation tests
- coverage verification and upload
- dependency review
- Gradle wrapper validation
- secret scanning
- CodeQL analysis
- OpenSSF Scorecard
- GitHub Pages publication

## GitHub repository metadata

Use [scripts/github/configure-repository.ps1](scripts/github/configure-repository.ps1) if you want GitHub CLI to apply the repository description, homepage, and topics automatically after cloning.

Recommended GitHub description:
`Android acoustic ranging demo that emits near-ultrasonic pulses, estimates the nearest reflection, and teaches the codebase through beginner-first docs.`

Recommended homepage:
`https://gedrocht.github.io/Mosmena/`

## Learn more

- Beginner docs: [docs/index.md](docs/index.md)
- Wiki instructions: [wiki/README.md](wiki/README.md)
- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Security policy: [SECURITY.md](SECURITY.md)
