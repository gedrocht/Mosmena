Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-AndroidSdkPathIfAvailable {
  $candidateAndroidSdkPaths = @(
    $env:ANDROID_SDK_ROOT,
    $env:ANDROID_HOME,
    (Join-Path $env:LOCALAPPDATA "Android\Sdk"),
    (Join-Path $env:USERPROFILE "AppData\Local\Android\Sdk")
  ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }

  foreach ($candidateAndroidSdkPath in $candidateAndroidSdkPaths) {
    if (Test-Path $candidateAndroidSdkPath) {
      return $candidateAndroidSdkPath
    }
  }

  return $null
}

function ConvertTo-GradlePropertyPath {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path
  )

  return $Path.Replace("\", "\\").Replace(":", "\:")
}

function Remove-EmptyDirectoryIfPresent {
  param(
    [Parameter(Mandatory = $true)]
    [string]$DirectoryPath
  )

  if (-not (Test-Path $DirectoryPath)) {
    return
  }

  $directoryEntries = @(Get-ChildItem -Path $DirectoryPath -Force)
  if ($directoryEntries.Count -eq 0) {
    Remove-Item -Path $DirectoryPath -Force
  }
}

function Sync-LocalPropertiesSdkDirectory {
  param(
    [Parameter(Mandatory = $true)]
    [string]$RepositoryRootPath,
    [Parameter(Mandatory = $true)]
    [string]$AndroidSdkPath
  )

  $localPropertiesPath = Join-Path $RepositoryRootPath "local.properties"
  $sdkDirectoryLine = "sdk.dir=$(ConvertTo-GradlePropertyPath -Path $AndroidSdkPath)"
  $existingLines =
    if (Test-Path $localPropertiesPath) {
      @(Get-Content -Path $localPropertiesPath)
    } else {
      @()
    }

  $updatedLines = @($sdkDirectoryLine) + @(
    $existingLines | Where-Object { $_ -notmatch '^sdk\.dir=' }
  )

  if (($existingLines -join "`n") -eq ($updatedLines -join "`n")) {
    return [pscustomobject]@{
      Changed = $false
      LocalPropertiesPath = $localPropertiesPath
    }
  }

  Set-Content -Path $localPropertiesPath -Value $updatedLines -Encoding ASCII
  return [pscustomobject]@{
    Changed = $true
    LocalPropertiesPath = $localPropertiesPath
  }
}

function Ensure-AndroidSdkReady {
  param(
    [Parameter(Mandatory = $true)]
    [string]$RepositoryRootPath
  )

  $androidSdkPath = Get-AndroidSdkPathIfAvailable
  $localPropertiesPath = Join-Path $RepositoryRootPath "local.properties"

  if ($null -eq $androidSdkPath) {
    if (-not (Test-Path $localPropertiesPath)) {
      throw "Android SDK was not detected. Install Android Studio and the Android SDK, or run .\scripts\setup.ps1 after setting ANDROID_SDK_ROOT."
    }

    return [pscustomobject]@{
      AndroidSdkPath = $null
      LocalPropertiesPath = $localPropertiesPath
      LocalPropertiesChanged = $false
    }
  }

  $env:ANDROID_SDK_ROOT = $androidSdkPath
  if ([string]::IsNullOrWhiteSpace($env:ANDROID_HOME)) {
    $env:ANDROID_HOME = $androidSdkPath
  }

  $syncResult = Sync-LocalPropertiesSdkDirectory `
    -RepositoryRootPath $RepositoryRootPath `
    -AndroidSdkPath $androidSdkPath

  return [pscustomobject]@{
    AndroidSdkPath = $androidSdkPath
    LocalPropertiesPath = $syncResult.LocalPropertiesPath
    LocalPropertiesChanged = $syncResult.Changed
  }
}

function Invoke-MosmenaGradle {
  param(
    [Parameter(Mandatory = $true)]
    [string]$RepositoryRootPath,
    [Parameter(Mandatory = $true)]
    [string[]]$GradleArguments
  )

  $androidSdkState = Ensure-AndroidSdkReady -RepositoryRootPath $RepositoryRootPath

  if ($androidSdkState.LocalPropertiesChanged) {
    Write-Host "Updated local.properties to use Android SDK at:"
    Write-Host $androidSdkState.AndroidSdkPath
    Write-Host ""
  }

  Remove-EmptyDirectoryIfPresent -DirectoryPath (
    Join-Path $RepositoryRootPath "app\src\main\res\mipmap-anydpi-v26"
  )

  & (Join-Path $RepositoryRootPath "gradlew.bat") "--no-watch-fs" @GradleArguments
  if ($LASTEXITCODE -ne 0) {
    throw "Gradle command failed."
  }
}
