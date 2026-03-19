param(
  [switch]$Detached
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
$wikiDirectoryPath = Join-Path $repositoryRootPath "wiki"

Set-Location $wikiDirectoryPath

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
  throw "Docker was not found. Install Docker Desktop first, then rerun this script."
}

$dockerComposeArguments = @("compose", "up")
if ($Detached) {
  $dockerComposeArguments += "-d"
}

Write-Host "Starting the Mosmena local wiki..."
docker @dockerComposeArguments
