# Staging environment

Staging is a separate, allowlisted environment for Trusted Testers. It must
have its own Russian database, object-storage bucket, backup destination,
domains, signing keys, email/SMS credentials, MLS identity, and Relay.

No production record may be copied here. Configuration is injected by the
deployment platform; this directory must never contain credentials. The first
deployment definition is intentionally deferred until a hosting provider and
the Russian legal launch gates are resolved.
