$ErrorActionPreference = 'Stop'

$repositoryRoot = Split-Path -Parent $PSScriptRoot

Push-Location $repositoryRoot
try {
    & .\gradlew.bat clean test --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Server checks failed.' }
} finally {
    Pop-Location
}

Push-Location (Join-Path $repositoryRoot 'mod')
try {
    & .\gradlew.bat clean build --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Forge checks failed.' }
} finally {
    Pop-Location
}

Push-Location (Join-Path $repositoryRoot 'web')
try {
    & npx --yes pnpm@12.16.1 install --frozen-lockfile
    if ($LASTEXITCODE -ne 0) { throw 'Website dependencies failed.' }
    & npx --yes pnpm@12.16.1 run lint
    if ($LASTEXITCODE -ne 0) { throw 'Website lint failed.' }
    & npx --yes pnpm@12.16.1 run test
    if ($LASTEXITCODE -ne 0) { throw 'Website tests failed.' }
    & npx --yes pnpm@12.16.1 run build
    if ($LASTEXITCODE -ne 0) { throw 'Website build failed.' }
} finally {
    Pop-Location
}
