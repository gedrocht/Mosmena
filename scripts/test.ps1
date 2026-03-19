. (Join-Path $PSScriptRoot "common.ps1")

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRootPath

Write-Host "Running Mosmena unit tests and coverage verification"
Invoke-MosmenaGradle `
  -RepositoryRootPath $repositoryRootPath `
  -GradleArguments @("testDebugUnitTest", "koverXmlReport", "koverVerify")
