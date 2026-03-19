# Getting Started

## Prerequisites

- Java Development Kit 17
- Android SDK platform 36
- Android build-tools 36.0.0
- Android platform-tools
- An Android device or emulator for manual testing
- Python 3 if you want to build or serve the docs site
- Docker Desktop if you want to run the local wiki

## Recommended order for complete beginners

1. Run `doctor` to see which tools are missing.
2. Run `setup` to prepare local configuration.
3. Run `build`.
4. Run `run-android`.
5. Run `test`.
6. Run `quality`.
7. Run `docs`.
8. Run `wiki`.

See [Beginner Quickstart](beginner-quickstart.md) for the exact commands.

## Clone and inspect

```bash
git clone https://github.com/gedrocht/Mosmena.git
cd Mosmena
```

## Use the helper scripts

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

## Run the quality gates

```bash
./gradlew detekt ktlintCheck lintDebug testDebugUnitTest koverVerify
```

## Generate API docs

```bash
./gradlew :app:dokkaGeneratePublicationHtml
```

The generated HTML appears in `app/build/dokka/html/`.

## Run the wiki locally

Open the repository file `wiki/README.md` for the DokuWiki startup instructions.
