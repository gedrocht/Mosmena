Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$requiredFiles = @(
  ".editorconfig",
  ".gitattributes",
  ".gitignore",
  "README.md",
  "LICENSE",
  "SECURITY.md",
  "CONTRIBUTING.md",
  "mkdocs.yml",
  ".github/CODEOWNERS",
  ".github/codeql/codeql-config.yml",
  ".github/ISSUE_TEMPLATE/bug_report.yml",
  ".github/ISSUE_TEMPLATE/feature_request.yml",
  ".github/pull_request_template.md",
  ".github/dependabot.yml",
  ".github/workflows/ci.yml",
  ".github/workflows/pages.yml",
  ".github/workflows/security.yml",
  ".github/workflows/review.yml",
  "app/build.gradle.kts",
  "app/src/main/AndroidManifest.xml",
  "docs/index.md",
  "docs/beginner-quickstart.md",
  "scripts/README.md",
  "scripts/help.ps1",
  "scripts/help.sh",
  "scripts/doctor.ps1",
  "scripts/doctor.sh",
  "scripts/setup.ps1",
  "scripts/setup.sh",
  "scripts/build.ps1",
  "scripts/build.sh",
  "scripts/run-android.ps1",
  "scripts/run-android.sh",
  "scripts/test.ps1",
  "scripts/test.sh",
  "scripts/quality.ps1",
  "scripts/quality.sh",
  "scripts/docs.ps1",
  "scripts/docs.sh",
  "scripts/wiki.ps1",
  "scripts/wiki.sh",
  "wiki/docker-compose.yml",
  "scripts/github/configure-repository.ps1"
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
