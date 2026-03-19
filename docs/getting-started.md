# Getting Started

## Prerequisites

- Java Development Kit 17
- Android SDK platform 36
- Android build-tools 36.0.0
- An Android device or emulator for manual testing

## Clone and inspect

```bash
git clone https://github.com/gedrocht/Mosmena.git
cd Mosmena
```

## Run the quality gates

```bash
./gradlew detekt ktlintCheck lintDebug testDebugUnitTest koverVerify
```

## Generate API docs

```bash
./gradlew dokkaHtml
```

The generated HTML appears in `app/build/dokka/html/`.

## Run the wiki locally

Open the repository file `wiki/README.md` for the DokuWiki startup instructions.
