# Development setup

The repository is designed to live outside OneDrive at
`C:\dev\keep-together` on the primary Windows development machine.

## Required toolchains

- JDK 17 for Forge and Java 17-compatible shared artifacts.
- JDK 25 for Backend and Relay.
- Node 24 LTS and pnpm for Website and TypeScript generation.
- Docker Desktop using WSL2 for PostgreSQL, object storage, mail capture, and
  Testcontainers.
- Git.
- IntelliJ IDEA Community for Java/Forge and VS Code for TypeScript/docs are the
  recommended editors.

On Windows, Docker Desktop requires WSL2. If `wsl --status` reports that WSL is
not installed, open an elevated PowerShell, run
`wsl --install --no-distribution`, restart Windows, then start Docker Desktop.
This is a one-time machine setup and cannot be completed from an unelevated
project shell.

The existing Java 27 and Node 26 installations are not project runtimes. Build
scripts must pin toolchains rather than depend on whichever executable appears
first on `PATH`.

## Local memory budget

The primary machine has 16 GiB RAM. Use these starting limits:

- first Minecraft client: approximately 3 GiB;
- second test client: 2-3 GiB;
- all containers combined: at most 4 GiB;
- IDEs and operating system: remaining memory.

Run heavy load tests on staging infrastructure rather than interpreting local
paging behavior as network performance.

## Environments

- `local`: disposable local data and deterministic development Adapters.
- `staging`: allowlisted Trusted Testers and independent credentials/data.
- `production`: created only for public beta after legal gates.

Never copy production data into local or staging. Every environment has separate
databases, buckets, domains, signing keys, MLS identity, Relay, email, and SMS
configuration.

## Bootstrap status

M0 provides buildable placeholder applications and verification commands. It
does not provide product functionality.

From the repository root on Windows:

```powershell
.\scripts\check.ps1
```

The script runs the server tests through the root Gradle wrapper, the independent
Forge build through `mod/gradlew.bat`, then the website lint, test, and production
build through the pinned pnpm release.

To start disposable local dependencies:

```powershell
.\scripts\local-infra.ps1 up
```

On first use, the script creates the ignored `infra/local/.env` with random
local-only credentials. It never prints those credentials.

Mailpit is then available on `http://127.0.0.1:8025` and the RustFS console on
`http://127.0.0.1:9001`. Stop the containers with
`.\scripts\local-infra.ps1 down`. Removing Docker volumes is deliberately not
part of that script because it destroys local data.
