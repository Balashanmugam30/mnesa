<#
.SYNOPSIS
    MNESA Developer Automation Tool
.DESCRIPTION
    Manages local Docker services, health status, and multi-tier test execution.
#>

param(
    [Parameter(Position=0)]
    [ValidateSet("up", "down", "status", "test", "test-backend", "test-android", "test-ai", "test-web", "build")]
    [string]$Command = "status"
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = Split-Path -Parent $ScriptDir
$ComposeFile = Join-Path $RootDir "infrastructure\docker\compose.dev.yml"

function Start-Services {
    Write-Host "==> Starting MNESA infrastructure services (PostgreSQL, Valkey, MinIO)..." -ForegroundColor Cyan
    docker compose -f $ComposeFile up -d postgres valkey minio
    docker compose -f $ComposeFile ps
}

function Stop-Services {
    Write-Host "==> Stopping MNESA infrastructure services..." -ForegroundColor Cyan
    docker compose -f $ComposeFile down
}

function Check-Status {
    Write-Host "==> MNESA Infrastructure Status:" -ForegroundColor Cyan
    docker compose -f $ComposeFile ps
}

function Test-Backend {
    Write-Host "`n==> Running Spring Boot Modular Monolith Tests..." -ForegroundColor Cyan
    Push-Location (Join-Path $RootDir "backend")
    try {
        $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
        .\gradlew.bat test
    } finally {
        Pop-Location
    }
}

function Test-Android {
    Write-Host "`n==> Running Android Unit Tests & Assembling Debug APK..." -ForegroundColor Cyan
    Push-Location (Join-Path $RootDir "android")
    try {
        $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
        .\gradlew.bat testDebugUnitTest assembleDebug
    } finally {
        Pop-Location
    }
}

function Test-AiService {
    Write-Host "`n==> Running AI Service Pytest Suite..." -ForegroundColor Cyan
    Push-Location (Join-Path $RootDir "ai-service")
    try {
        .\.venv\Scripts\pytest -v
    } finally {
        Pop-Location
    }
}

function Test-Web {
    Write-Host "`n==> Building Next.js Web Platform..." -ForegroundColor Cyan
    Push-Location (Join-Path $RootDir "website")
    try {
        npm run build
    } finally {
        Pop-Location
    }
}

switch ($Command) {
    "up" { Start-Services }
    "down" { Stop-Services }
    "status" { Check-Status }
    "test-backend" { Test-Backend }
    "test-android" { Test-Android }
    "test-ai" { Test-AiService }
    "test-web" { Test-Web }
    "test" {
        Test-Backend
        Test-AiService
        Test-Android
        Test-Web
        Write-Host "`nALL MNESA TIER TESTS PASSED SUCCESSFULLY!" -ForegroundColor Green
    }
    "build" {
        Test-Backend
        Test-Android
        Test-Web
        Write-Host "`nALL MNESA BUILDS COMPLETED SUCCESSFULLY!" -ForegroundColor Green
    }
}
