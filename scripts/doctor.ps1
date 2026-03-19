Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-CommandPathIfAvailable {
  param(
    [Parameter(Mandatory = $true)]
    [string]$CommandName
  )

  $resolvedCommand = Get-Command $CommandName -ErrorAction SilentlyContinue
  if ($null -eq $resolvedCommand) {
    return $null
  }

  return $resolvedCommand.Source
}

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

function Write-CheckResult {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Label,
    [Parameter(Mandatory = $true)]
    [bool]$Passed,
    [Parameter(Mandatory = $true)]
    [string]$Details
  )

  $statusText = if ($Passed) { "OK" } else { "MISSING" }
  Write-Host ("[{0}] {1} - {2}" -f $statusText, $Label, $Details)
}

$javaCommandPath = Get-CommandPathIfAvailable -CommandName "java"
$pythonCommandPath = Get-CommandPathIfAvailable -CommandName "python"
$dockerCommandPath = Get-CommandPathIfAvailable -CommandName "docker"
$androidSdkPath = Get-AndroidSdkPathIfAvailable
$adbCommandPath = Get-CommandPathIfAvailable -CommandName "adb"

if ($null -eq $adbCommandPath -and $null -ne $androidSdkPath) {
  $adbPathInsideAndroidSdk = Join-Path $androidSdkPath "platform-tools\adb.exe"
  if (Test-Path $adbPathInsideAndroidSdk) {
    $adbCommandPath = $adbPathInsideAndroidSdk
  }
}

Write-Host "Mosmena prerequisite check"
Write-Host "==========================="
Write-Host ""

Write-CheckResult -Label "Java 17 or newer" -Passed ($null -ne $javaCommandPath) -Details (
  if ($null -ne $javaCommandPath) { $javaCommandPath } else { "Install Android Studio or a JDK 17 distribution." }
)
Write-CheckResult -Label "Android SDK" -Passed ($null -ne $androidSdkPath) -Details (
  if ($null -ne $androidSdkPath) { $androidSdkPath } else { "Install Android Studio, then install Android SDK platform 36 and build-tools 36.0.0." }
)
Write-CheckResult -Label "adb" -Passed ($null -ne $adbCommandPath) -Details (
  if ($null -ne $adbCommandPath) { $adbCommandPath } else { "Install Android platform-tools or let Android Studio install them." }
)
Write-CheckResult -Label "Python" -Passed ($null -ne $pythonCommandPath) -Details (
  if ($null -ne $pythonCommandPath) { $pythonCommandPath } else { "Install Python 3 if you want to build or serve the docs site." }
)
Write-CheckResult -Label "Docker" -Passed ($null -ne $dockerCommandPath) -Details (
  if ($null -ne $dockerCommandPath) { $dockerCommandPath } else { "Install Docker Desktop if you want to run the local DokuWiki." }
)

Write-Host ""
Write-Host "Suggested next commands"
Write-Host "-----------------------"
Write-Host ".\scripts\setup.ps1"
Write-Host ".\scripts\build.ps1"
Write-Host ".\scripts\run-android.ps1"
Write-Host ".\scripts\test.ps1"
Write-Host ".\scripts\docs.ps1 -Serve"
Write-Host ".\scripts\wiki.ps1 -Detached"
