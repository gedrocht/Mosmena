param(
  [switch]$IncludeInstrumentationTests
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

$gradleArguments = @(
  "clean",
  "detekt",
  "ktlintCheck",
  "lintDebug",
  "testDebugUnitTest",
  "koverVerify",
  ":app:dokkaHtml"
)

if ($IncludeInstrumentationTests) {
  $gradleArguments += "connectedDebugAndroidTest"
}

Write-Host "Running Mosmena quality suite with Gradle tasks: $($gradleArguments -join ', ')"
& "$repositoryRootPath\gradlew.bat" @gradleArguments
