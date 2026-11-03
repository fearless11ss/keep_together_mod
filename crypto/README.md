# CryptoEngine

CryptoEngine hides MLS and Attachment encryption behind one small Interface.
The planned implementation uses RFC 9420 through Bouncy Castle 1.86+ and Tink
Streaming AEAD for images.

No custom cryptographic primitive is permitted. Official test vectors, fuzzing,
dependency pinning, rapid security updates, and independent review are required
before production security claims.
