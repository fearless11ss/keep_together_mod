param(
    [ValidateSet('up', 'down', 'status')]
    [string] $Action = 'up'
)

$ErrorActionPreference = 'Stop'
$localDirectory = Join-Path (Split-Path -Parent $PSScriptRoot) 'infra\local'
$environmentFile = Join-Path $localDirectory '.env'

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker Desktop is not installed or docker is not on PATH.'
}

if (-not (Test-Path -LiteralPath $environmentFile)) {
    $postgresPassword = [Convert]::ToHexString(
        [Security.Cryptography.RandomNumberGenerator]::GetBytes(24)
    ).ToLowerInvariant()
    $objectStorageSecret = [Convert]::ToHexString(
        [Security.Cryptography.RandomNumberGenerator]::GetBytes(24)
    ).ToLowerInvariant()
    $environmentLines = @(
        'POSTGRES_DB=keep_together'
        'POSTGRES_USER=keep_together'
        "POSTGRES_PASSWORD=$postgresPassword"
        'RUSTFS_ACCESS_KEY=keep-together-local'
        "RUSTFS_SECRET_KEY=$objectStorageSecret"
    )
    [IO.File]::WriteAllLines(
        $environmentFile,
        $environmentLines,
        [Text.UTF8Encoding]::new($false)
    )
    Write-Output 'Created infra/local/.env with random local-only credentials.'
}

Push-Location $localDirectory
try {
    switch ($Action) {
        'up' { & docker compose up --detach --wait }
        'down' { & docker compose down }
        'status' { & docker compose ps }
    }
    if ($LASTEXITCODE -ne 0) { throw "Docker Compose action '$Action' failed." }
} finally {
    Pop-Location
}
