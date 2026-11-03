# Production environment

Production is not deployable during M0. Creating it is blocked by the legal and
security gates in `docs/legal-launch-gates.md`.

The approved topology will keep the Data server, object storage, and backups in
Russia. Relay capacity is isolated from Data capacity. Credentials, keys, and
personal data are never committed to this repository, and capacity changes
require a reviewed budget decision rather than unbounded autoscaling.
