Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

Write-Host "Running Mosmena unit tests and coverage verification"
& "$repositoryRootPath\gradlew.bat" testDebugUnitTest koverXmlReport koverVerify
