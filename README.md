# Mosmena

[![CI](https://github.com/gedrocht/Mosmena/actions/workflows/ci.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/ci.yml)
[![Security](https://github.com/gedrocht/Mosmena/actions/workflows/security.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/security.yml)
[![Docs](https://github.com/gedrocht/Mosmena/actions/workflows/pages.yml/badge.svg)](https://github.com/gedrocht/Mosmena/actions/workflows/pages.yml)

Mosmena is an Android demonstration project that emits a short near-ultrasonic pulse from the phone speaker, records the response through the microphone, estimates the nearest acoustic reflection, and visualizes the result.

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
- `scripts/ci/`: repository validation scripts
- `scripts/github/`: optional GitHub repository configuration helpers

## Local development

### Requirements

- Java Development Kit 17
- Android SDK platform 36
- Android build-tools 36.0.0

### Common commands

```bash
./gradlew lintDebug
./gradlew detekt ktlintCheck
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
./gradlew koverXmlReport koverVerify
./gradlew dokkaHtml
```

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

## Learn more

- Beginner docs: [docs/index.md](docs/index.md)
- Wiki instructions: [wiki/README.md](wiki/README.md)
- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Security policy: [SECURITY.md](SECURITY.md)
