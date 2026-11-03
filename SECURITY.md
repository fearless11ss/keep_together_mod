# Security policy

## Project status

Keep Together is pre-alpha and has no supported production version. Do not use
it for sensitive communications or expose a development deployment to the
public Internet.

## Reporting a vulnerability

Use GitHub's private vulnerability reporting / Security Advisory feature for
this repository. Do not open a public issue with exploit details, secrets, user
data, or a working proof of concept.

A dedicated security email will be published before public beta after the
project domain is registered. Until then, GitHub private reporting is the only
documented channel.

Include, when possible:

- affected commit or version;
- affected Module and Interface;
- prerequisites and reproduction steps;
- expected and observed impact;
- minimal proof of concept;
- suggested mitigation, if known;
- whether disclosure is time-sensitive.

## Response process

1. Acknowledge the report privately.
2. Reproduce it in an isolated environment.
3. Assess affected data, versions, and legal-notification duties.
4. Prepare and review a fix and regression tests.
5. Revoke unsafe sessions, keys, tickets, or releases when necessary.
6. Publish a signed fix before coordinated disclosure.
7. Credit the reporter unless they request anonymity.

There is no bug bounty or guaranteed response-time SLA during pre-alpha.

## Security claims

The intended design uses MLS end-to-end encryption, but implementation quality
has not yet been audited. Open source, standard algorithms, and test vectors do
not replace independent review.
