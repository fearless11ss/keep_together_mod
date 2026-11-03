# Use MLS end-to-end encryption without an operator backdoor

Direct and Group Conversations will use RFC 9420 MLS through a reviewed
CryptoEngine, with each Device represented as a member and Attachments encrypted
separately through a high-level streaming AEAD implementation. Backend stores
and orders ciphertext but receives no master key; if this model cannot lawfully
launch in a target jurisdiction, Messaging remains disabled rather than being
marketed as E2E while retaining hidden operator decryption.
