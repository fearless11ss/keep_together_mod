[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('owner', 'guest')]
    [string]$Role,

    [switch]$ValidateOnly
)

$ErrorActionPreference = 'Stop'

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$modRoot = Join-Path $repositoryRoot 'mod'
$gradleWrapper = Join-Path $modRoot 'gradlew.bat'
if (-not (Test-Path -LiteralPath $gradleWrapper -PathType Leaf)) {
    throw "Forge Gradle wrapper not found at $gradleWrapper"
}

$taskName = if ($Role -eq 'owner') { 'runOwnerClient' } else { 'runGuestClient' }
if ($ValidateOnly) {
    Write-Output "M1 client launcher is ready for role: $Role"
    return
}

$secureTicket = Read-Host "Paste the $($Role.ToUpperInvariant()) ticket" -AsSecureString
$ticketPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureTicket)
try {
    $ticket = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ticketPointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ticketPointer)
}
if ([string]::IsNullOrWhiteSpace($ticket)) {
    throw 'Relay Ticket must not be blank.'
}

$env:KT_M1_MODE = $Role
$env:KT_M1_RELAY_HOST = '127.0.0.1'
$env:KT_M1_RELAY_PORT = '25570'
$env:KT_M1_TICKET = $ticket

Push-Location $modRoot
try {
    & $gradleWrapper $taskName --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw "M1 $Role client failed."
    }
} finally {
    Pop-Location
    Remove-Item Env:KT_M1_TICKET -ErrorAction SilentlyContinue
}
