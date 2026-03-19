. (Join-Path $PSScriptRoot "common.ps1")

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
  throw "adb was not found. Install Android platform-tools or open Android Studio and install them first."
}

$connectedDeviceLines = adb devices | Select-String -Pattern "device$"
if ($connectedDeviceLines.Count -eq 0) {
  throw "No Android device or emulator is connected. Start an emulator in Android Studio or connect a physical device with USB debugging enabled."
}

Write-Host "Installing the debug build on the connected Android device or emulator..."
Invoke-MosmenaGradle -RepositoryRootPath $repositoryRootPath -GradleArguments @("installDebug")

Write-Host "Launching Mosmena..."
adb shell am start -n com.gedrocht.mosmena/.ui.MainActivity
