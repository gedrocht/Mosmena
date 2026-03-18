Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$requiredFiles = @(
  ".editorconfig",
  ".gitattributes",
  ".gitignore",
  "README.md",
  "SECURITY.md",
  ".github/CODEOWNERS",
  ".github/pull_request_template.md",
  ".github/dependabot.yml",
  ".github/workflows/ci.yml",
  ".github/workflows/security.yml",
  ".github/workflows/review.yml"
)

$missing = @()
foreach ($file in $requiredFiles) {
  if (-not (Test-Path -LiteralPath $file)) {
    $missing += $file
  }
}

if ($missing.Count -gt 0) {
  Write-Error ("Missing required repository files:`n - " + ($missing -join "`n - "))
}

Write-Host "Repository policy files are present."
