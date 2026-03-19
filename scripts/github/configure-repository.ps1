param(
  [string]$RepositoryName = "gedrocht/Mosmena"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repositoryDescription =
  "Android acoustic ranging demo that emits near-ultrasonic pulses, estimates the nearest reflection, and teaches the codebase through beginner-first docs."
$repositoryHomepage = "https://gedrocht.github.io/Mosmena/"
$repositoryTopics = @(
  "android",
  "kotlin",
  "acoustics",
  "signal-processing",
  "ultrasonic",
  "testing",
  "documentation"
)

gh repo edit $RepositoryName `
  --description $repositoryDescription `
  --homepage $repositoryHomepage `
  --enable-issues `
  --enable-projects=false `
  --enable-discussions

$topicArguments = $repositoryTopics | ForEach-Object { "--add-topic=$_"} 
gh repo edit $RepositoryName @topicArguments

Write-Host "Repository metadata updated for $RepositoryName"
