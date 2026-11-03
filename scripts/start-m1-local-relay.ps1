[CmdletBinding()]
param(
    [switch]$TicketsOnly
)

$ErrorActionPreference = 'Stop'

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$env:KT_RELAY_TICKET_SECRET = & (Join-Path $PSScriptRoot 'new-m1-relay-secret.ps1')
$env:KT_M1_TICKET_TTL_MINUTES = '60'

Push-Location $repositoryRoot
try {
    & .\gradlew.bat :relay:issueM1Tickets --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw 'M1 Relay ticket generation failed.'
    }

    if ($TicketsOnly) {
        return
    }

    Write-Host ''
    Write-Host 'Keep this window open. Starting the loopback M1 Relay...'
    & .\gradlew.bat :relay:run --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw 'M1 Relay failed.'
    }
} finally {
    Pop-Location
}
