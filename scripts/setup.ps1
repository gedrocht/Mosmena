param(
  [switch]$SkipLocalProperties
)

. (Join-Path $PSScriptRoot "common.ps1")

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

Write-Host "Mosmena beginner setup"
Write-Host "======================"
Write-Host ""
Write-Host "Step 1: Install Android Studio if you do not already have it."
Write-Host "Step 2: In Android Studio, install Android SDK platform 36, build-tools 36.0.0, and platform-tools."
Write-Host "Step 3: Install Python 3 if you want to build or serve the docs site."
Write-Host "Step 4: Install Docker Desktop if you want to run the local wiki."
Write-Host ""

$androidSdkPath = Get-AndroidSdkPathIfAvailable

if (-not $SkipLocalProperties -and $null -ne $androidSdkPath) {
  $androidSdkState = Ensure-AndroidSdkReady -RepositoryRootPath $repositoryRootPath
  if ($androidSdkState.LocalPropertiesChanged) {
    Write-Host "Updated local.properties to use Android SDK at:"
    Write-Host $androidSdkPath
  } else {
    Write-Host "local.properties is already configured."
  }
  Write-Host ""
} elseif (-not $SkipLocalProperties -and (Test-Path (Join-Path $repositoryRootPath "local.properties"))) {
  Write-Host "local.properties already exists, but no Android SDK path was detected automatically."
  Write-Host ""
} else {
  Write-Host "Skipped local.properties creation."
  Write-Host ""
}

Write-Host "Next commands"
Write-Host "-------------"
Write-Host ".\scripts\doctor.ps1"
Write-Host ".\scripts\build.ps1"
Write-Host ".\scripts\run-android.ps1"
Write-Host ".\scripts\test.ps1"
