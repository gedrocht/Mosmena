param(
  [switch]$SkipLocalProperties
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-AndroidSdkPathIfAvailable {
  $candidateAndroidSdkPaths = @(
    $env:ANDROID_SDK_ROOT,
    $env:ANDROID_HOME,
    (Join-Path $env:LOCALAPPDATA "Android\Sdk"),
    (Join-Path $env:USERPROFILE "AppData\Local\Android\Sdk")
  ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }

  foreach ($candidateAndroidSdkPath in $candidateAndroidSdkPaths) {
    if (Test-Path $candidateAndroidSdkPath) {
      return $candidateAndroidSdkPath
    }
  }

  return $null
}

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
$localPropertiesPath = Join-Path $repositoryRootPath "local.properties"

if (-not $SkipLocalProperties -and $null -ne $androidSdkPath -and -not (Test-Path $localPropertiesPath)) {
  $escapedAndroidSdkPath = $androidSdkPath.Replace("\", "\\")
  Set-Content -Path $localPropertiesPath -Value "sdk.dir=$escapedAndroidSdkPath"
  Write-Host "Created local.properties pointing at:"
  Write-Host $androidSdkPath
  Write-Host ""
} elseif (-not $SkipLocalProperties -and Test-Path $localPropertiesPath) {
  Write-Host "local.properties already exists."
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
