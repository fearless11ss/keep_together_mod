# M1 world-tunnel spike

This spike proves one narrow path: a guest Forge client connects to an owner's
IntegratedServer through the Relay without either Minecraft client accepting an
inbound Internet connection. It is not a production networking feature.

## Safety boundary

- Use loopback on one computer first, or a trusted private LAN.
- Do not expose this Relay to the public Internet. M1 has neither TLS nor inner
  end-to-end encryption, so Relay can observe and modify Minecraft traffic.
- Tickets are bearer credentials. Do not commit them, paste them into issues,
  or reuse them. They expire after 15 minutes by default and are consumed once.
- One ticket pair admits exactly one owner and one guest. Multi-guest sessions,
  revocation, quotas, reconnects, TLS, and inner encryption belong to M4.
- `KT_RELAY_TICKET_SECRET` is the Relay signing secret. Keep it out of files and
  shell history. The commands below keep it only in process environments.

## Prerequisites

- Windows PowerShell.
- The repository checks have passed with `./scripts/check.ps1`.
- Three terminals: one Relay, one owner client, and one guest client.

The Forge runs use separate `mod/run-owner` and `mod/run-guest` directories and
different offline development usernames. This avoids client-state collisions.

For the current Windows checkout, first run this in every new terminal:

```powershell
Set-Location 'C:\dev\keep-together'
```

Verify that `Test-Path .\gradlew.bat` returns `True` before continuing. The
Desktop folder named `social mod` is not the repository root.

## 1. Issue tickets and start Relay

In terminal 1, run the local helper. It generates an in-memory secret, prints
one owner/guest ticket pair with a 60-minute development lifetime, and starts a
loopback-only Relay. It supports Windows PowerShell 5.1 and newer:

```powershell
.\scripts\start-m1-local-relay.ps1
```

The expected address is `127.0.0.1:25570`. Leave the process running and keep
the two ticket lines visible. Do not publish them. The session ID and expiry are
diagnostic values, not client input.

## 2. Start the owner

In terminal 2:

```powershell
Set-Location mod
$env:KT_M1_MODE = 'owner'
$env:KT_M1_RELAY_HOST = '127.0.0.1'
$env:KT_M1_RELAY_PORT = '25570'
$env:KT_M1_TICKET = Read-Host 'Paste KT_M1_OWNER_TICKET'
.\gradlew.bat runOwnerClient --console=plain
```

Open any single-player world. The mod publishes the IntegratedServer locally
and logs that the owner tunnel is waiting for its paired guest.

## 3. Start and connect the guest

In terminal 3:

```powershell
Set-Location mod
$env:KT_M1_MODE = 'guest'
$env:KT_M1_RELAY_HOST = '127.0.0.1'
$env:KT_M1_RELAY_PORT = '25570'
$env:KT_M1_TICKET = Read-Host 'Paste KT_M1_GUEST_TICKET'
.\gradlew.bat runGuestClient --console=plain
```

Find this guest log line and copy its actual port:

```text
Keep Together M1 guest tunnel listening at 127.0.0.1:<port>
```

In the guest client, choose **Multiplayer → Direct Connection** and enter that
address. The first local connection causes both tickets to be authenticated and
the Relay to pair the streams. Success means the Guest player joins the Owner
world and can move or interact normally.

## Expected limitations and cleanup

Closing either client or leaving the owner world ends the tunnel. A second
connection needs a newly issued pair. Stop Relay with `Ctrl+C`, then close all
terminals so their environment variables disappear.

If a ticket expires before connection, issue a fresh pair and restart both
client processes. Useful diagnostics are in `mod/run-owner/logs/latest.log` and
`mod/run-guest/logs/latest.log`; remove ticket values before sharing logs.
