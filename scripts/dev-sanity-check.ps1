param(
    [string]$RepoRoot = "D:\kamesh-codes\BrahmiBhojan",
    [string]$JavaHome = "C:\Program Files\Java\jdk-21.0.11"
)

$ErrorActionPreference = "Stop"

function Write-Step($message) {
    Write-Host "[CHECK] $message"
}

function Write-Pass($message) {
    Write-Host "[PASS]  $message"
}

function Write-Warn($message) {
    Write-Host "[WARN]  $message"
}

Write-Step "Repository root: $RepoRoot"
if (-not (Test-Path $RepoRoot)) {
    throw "Repository root not found: $RepoRoot"
}

if (Test-Path $JavaHome) {
    $env:JAVA_HOME = $JavaHome
    if (-not $env:Path.StartsWith("$JavaHome\bin")) {
        $env:Path = "$JavaHome\bin;" + $env:Path
    }
    Write-Pass "JAVA_HOME set to $JavaHome"
} else {
    Write-Warn "Expected Java home not found at $JavaHome; using existing JAVA_HOME"
}

Write-Step "Java version"
java -version
Write-Pass "Java command available"

Write-Step "Docker daemon status"
docker info | Out-Null
Write-Pass "Docker daemon reachable"

Write-Step "Compose service status"
Set-Location $RepoRoot
docker compose ps
Write-Pass "Compose command executed"

Write-Step "Maven command availability"
$mvnPath = Join-Path $RepoRoot "tools\apache-maven-3.9.9\bin\mvn.cmd"
if (-not (Test-Path $mvnPath)) {
    throw "Maven binary not found at $mvnPath"
}
& $mvnPath -v
Write-Pass "Maven command available"

Write-Host ""
Write-Host "Dev sanity check completed."
