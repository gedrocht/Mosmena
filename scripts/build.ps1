param(
  [switch]$Clean
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

$gradleArguments = @()
if ($Clean) {
  $gradleArguments += "clean"
}
$gradleArguments += @("assembleDebug", "assembleRelease")

Write-Host "Building Mosmena with Gradle tasks: $($gradleArguments -join ', ')"
& "$repositoryRootPath\gradlew.bat" @gradleArguments
