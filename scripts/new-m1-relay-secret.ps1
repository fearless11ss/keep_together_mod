$ErrorActionPreference = 'Stop'

$secretBytes = New-Object byte[] 32
$generator = [Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $generator.GetBytes($secretBytes)
} finally {
    $generator.Dispose()
}

[Convert]::ToBase64String($secretBytes)
