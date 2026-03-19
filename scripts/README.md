# Scripts

This folder exists to make common project tasks obvious for beginners.

## Start here

- `doctor`: checks whether your machine has the tools needed to build and run the project
- `setup`: helps you prepare local configuration such as `local.properties`
- `build`: builds the Android application
- `run-android`: installs and launches the Android app on a connected device or emulator
- `test`: runs unit tests and coverage verification
- `quality`: runs the main local quality suite
- `docs`: builds or serves the GitHub Pages documentation site
- `wiki`: starts the local DokuWiki instance

## Windows PowerShell examples

```powershell
.\scripts\doctor.ps1
.\scripts\setup.ps1
.\scripts\build.ps1
.\scripts\run-android.ps1
.\scripts\test.ps1
.\scripts\quality.ps1
.\scripts\docs.ps1 -Serve
.\scripts\wiki.ps1 -Detached
```

## macOS or Linux examples

```bash
./scripts/doctor.sh
./scripts/setup.sh
./scripts/build.sh
./scripts/run-android.sh
./scripts/test.sh
./scripts/quality.sh
./scripts/docs.sh --serve
./scripts/wiki.sh --detached
```
