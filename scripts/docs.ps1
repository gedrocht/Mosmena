param(
  [switch]$Serve
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryRootPath = Split-Path -Parent $PSScriptRoot
$documentationVirtualEnvironmentPath = Join-Path $repositoryRootPath ".venv-docs"
$documentationVirtualEnvironmentPythonPath = Join-Path $documentationVirtualEnvironmentPath "Scripts\python.exe"

Set-Location $repositoryRootPath

if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
  throw "Python was not found. Install Python 3 first, then rerun this script."
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
  throw "Java was not found. Install Java Development Kit 17 first, then rerun this script."
}

if (-not (Test-Path $documentationVirtualEnvironmentPythonPath)) {
  Write-Host "Creating a Python virtual environment for documentation tools..."
  python -m venv $documentationVirtualEnvironmentPath
}

Write-Host "Installing MkDocs dependencies into .venv-docs..."
& $documentationVirtualEnvironmentPythonPath -m pip install --upgrade pip
& $documentationVirtualEnvironmentPythonPath -m pip install mkdocs==1.6.1 mkdocs-material==9.5.34

Write-Host "Generating API reference with Dokka..."
& "$repositoryRootPath\gradlew.bat" ":app:dokkaGeneratePublicationHtml"
if ($LASTEXITCODE -ne 0) {
  throw "Dokka API documentation generation failed."
}

if ($Serve) {
  Write-Host "Serving documentation at http://127.0.0.1:8000"
  & $documentationVirtualEnvironmentPythonPath -m mkdocs serve
} else {
  Write-Host "Building documentation into the site directory..."
  & $documentationVirtualEnvironmentPythonPath -m mkdocs build --strict
}

if ($LASTEXITCODE -ne 0) {
  throw "MkDocs documentation build failed."
}
