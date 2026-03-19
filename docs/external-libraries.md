# External Libraries

## Timber

- Purpose: developer-friendly logging that still ends up in Logcat
- Usage in this repository: `InMemoryApplicationLogRecorder` mirrors messages through Timber and also stores them for the in-app log viewer
- Documentation: [Timber README](https://github.com/JakeWharton/timber)

## Detekt

- Purpose: Kotlin static analysis
- Usage in this repository: enforced in CI and configured in `config/detekt/detekt.yml`
- Documentation: [Detekt documentation](https://detekt.dev/)

## ktlint-gradle

- Purpose: style enforcement for Kotlin and Kotlin Gradle DSL files
- Usage in this repository: run through `ktlintCheck` in CI
- Documentation: [ktlint-gradle documentation](https://github.com/JLLeitschuh/ktlint-gradle)

## Kover

- Purpose: code coverage reporting and verification
- Usage in this repository: CI fails when coverage falls below the configured thresholds
- Documentation: [Kover Gradle plugin documentation](https://kotlin.github.io/kotlinx-kover/gradle-plugin/)

## Dokka

- Purpose: API reference generation from KDoc
- Usage in this repository: `./gradlew :app:dokkaGeneratePublicationHtml`
- Documentation: [Dokka introduction](https://kotlinlang.org/docs/dokka-introduction.html)

## MkDocs Material

- Purpose: GitHub Pages documentation site
- Usage in this repository: builds the beginner-focused docs under `docs/`
- Documentation: [MkDocs Material documentation](https://squidfunk.github.io/mkdocs-material/)

## DokuWiki

- Purpose: a second, more tutorial-heavy wiki layer
- Usage in this repository: served locally through Docker Compose in `wiki/`
- Documentation: [DokuWiki documentation](https://www.dokuwiki.org/dokuwiki)
