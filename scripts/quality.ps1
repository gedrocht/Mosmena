param(
  [switch]$Clean,
  [switch]$IncludeInstrumentationTests
)

. (Join-Path $PSScriptRoot "common.ps1")

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

$gradleArguments = @()
if ($Clean) {
  $gradleArguments += "clean"
}
$gradleArguments += @(
  "detekt",
  "ktlintCheck",
  "lintDebug",
  "testDebugUnitTest",
  "koverVerify",
  ":app:dokkaGeneratePublicationHtml"
)

if ($IncludeInstrumentationTests) {
  $gradleArguments += "connectedDebugAndroidTest"
}

Write-Host "Running Mosmena quality suite with Gradle tasks: $($gradleArguments -join ', ')"
Invoke-MosmenaGradle -RepositoryRootPath $repositoryRootPath -GradleArguments $gradleArguments
