# Infrastructure

Infrastructure definitions will cover local, staging, and production without
sharing credentials or data.

- Local uses Docker Desktop/WSL2 and disposable data.
- Staging is allowlisted for Trusted Testers.
- Production is created only after legal and security launch gates.

`local/compose.yaml` pins PostgreSQL, S3-compatible object storage, and a mail
capture service to loopback-only ports. Run
`../../scripts/local-infra.ps1 up` from this directory or
`scripts/local-infra.ps1 up` from the repository root.
The script creates the ignored `local/.env` with random local-only credentials
when it does not exist.

The first production topology uses separate Russian Data and Relay servers,
Russian object storage, and separate Russian backups. Automatic purchasing or
unbounded autoscaling is forbidden; budget and capacity changes require manual
approval.
