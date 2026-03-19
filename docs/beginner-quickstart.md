# Beginner Quickstart

This page is the shortest safe path from zero to a working local setup.

## Step 1: Open a terminal in the repository

On Windows, open PowerShell in the repository root.

On macOS or Linux, open Terminal in the repository root.

## Step 2: Ask the project what you are missing

Windows:

```powershell
.\scripts\doctor.ps1
```

macOS or Linux:

```bash
./scripts/doctor.sh
```

If something is marked `MISSING`, install that tool first and rerun the doctor command.

## Step 3: Prepare local configuration

Windows:

```powershell
.\scripts\setup.ps1
```

macOS or Linux:

```bash
./scripts/setup.sh
```

This script tries to create `local.properties` for you if it can find the Android SDK.

## Step 4: Build the Android app

Windows:

```powershell
.\scripts\build.ps1
```

macOS or Linux:

```bash
./scripts/build.sh
```

## Step 5: Run the Android app

Start an emulator in Android Studio or connect an Android phone with USB debugging enabled.

Then run:

Windows:

```powershell
.\scripts\run-android.ps1
```

macOS or Linux:

```bash
./scripts/run-android.sh
```

## Step 6: Run tests

Windows:

```powershell
.\scripts\test.ps1
```

macOS or Linux:

```bash
./scripts/test.sh
```

## Step 7: Run the quality suite

Windows:

```powershell
.\scripts\quality.ps1
```

macOS or Linux:

```bash
./scripts/quality.sh
```

If you also want instrumentation tests:

Windows:

```powershell
.\scripts\quality.ps1 -IncludeInstrumentationTests
```

macOS or Linux:

```bash
./scripts/quality.sh --include-instrumentation-tests
```

## Step 8: Run the docs site

Windows:

```powershell
.\scripts\docs.ps1 -Serve
```

macOS or Linux:

```bash
./scripts/docs.sh --serve
```

Then open `http://127.0.0.1:8000`.

## Step 9: Run the local wiki

Windows:

```powershell
.\scripts\wiki.ps1 -Detached
```

macOS or Linux:

```bash
./scripts/wiki.sh --detached
```

Then open `http://localhost:8080`.
