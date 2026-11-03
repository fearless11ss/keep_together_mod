# Contributing

Keep Together is pre-alpha. Contributions are welcome, but maintainers may
decline changes that do not fit the current milestone or deepen the existing
Modules.

## Before opening a change

1. Read `PRODUCT.md`, `ARCHITECTURE.md`, `GLOSSARY.md`, and the relevant ADRs.
2. Search existing issues and roadmap milestones.
3. Open a design discussion before changing a hard-to-reverse Interface,
   protocol, cryptographic flow, data owner, or deployment shape.
4. Do not include secrets, user data, Minecraft binaries, Mojang assets, or
   unlicensed third-party content.

## Design expectations

- Put substantial behavior behind a small Module Interface.
- Test observable behavior through the Interface.
- Keep framework and Adapter types out of core Interfaces.
- Do not query another Module's tables.
- Add a Seam only when at least two Adapters are justified.
- Use the canonical language in `GLOSSARY.md`.
- Record an ADR only for a hard-to-reverse, surprising, real trade-off.

## Security and cryptography

- Never implement a new cryptographic primitive.
- Changes to CryptoEngine, authentication, session handling, Relay framing, or
  update signing require negative tests and explicit security review.
- Do not put message content, keys, tokens, phone numbers, emails, object keys,
  or full IP addresses in logs or telemetry.
- Report vulnerabilities through the private process in `SECURITY.md`.

## AI-assisted contributions

Disclose material AI assistance in the pull request. A human contributor must
understand, review, test, and take responsibility for every submitted line.
AI output is not accepted as evidence that cryptography, licensing, or legal
requirements are correct. AI-generated project icons and gallery art are not
accepted.

## Developer Certificate of Origin

Every commit must include a sign-off line certifying the Developer Certificate
of Origin:

```text
Signed-off-by: Your Name <your.email@example.com>
```

Use `git commit -s` to add it. By signing off, you certify that you have the
right to submit the contribution under the repository's MIT License.

## Pull requests

- Keep changes scoped to one behavior or decision.
- Add or update tests and documentation.
- Describe user-visible behavior, failure modes, and security impact.
- Link the relevant issue and milestone.
- Ensure every required CI check passes.
