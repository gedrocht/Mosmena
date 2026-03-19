param(
  [switch]$Clean
)

. (Join-Path $PSScriptRoot "common.ps1")

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

$gradleArguments = @()
if ($Clean) {
  $gradleArguments += "clean"
}
$gradleArguments += @("assembleDebug", "assembleRelease")

Write-Host "Building Mosmena with Gradle tasks: $($gradleArguments -join ', ')"
Invoke-MosmenaGradle -RepositoryRootPath $repositoryRootPath -GradleArguments $gradleArguments
