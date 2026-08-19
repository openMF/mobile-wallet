# deployment/desktop/msi-signed — Signed Windows MSI (Azure Trusted Signing)

**Tier:** 2 (opt-in)
**Owning capability:** `desktop-azure-trusted-signing`
**Signing:** Azure Trusted Signing (OIDC federation, no long-lived signing secrets)
**Gating:** `requires_confirm: true`

## What this deploys

Microsoft-signed Windows MSI installer attached to a GitHub release. Replaces
the legacy EV-USB-token signing flow with Azure Trusted Signing (cloud-hosted
certs, OIDC federation from GH Actions).

## Setup

> **Note:** all `azure_ts_*` aliases are declared in `secrets-needs.yaml` but
> the rows are **pending in `SECRETS_ALIAS_REGISTRY`**. Add them via
> `/secrets request <alias>` when Azure Trusted Signing is bootstrapped for
> your org.

Required setup (one-time, per org):
1. Create an Azure Trusted Signing account + cert profile.
2. Create an Azure App registration with federated credentials trusting your
   GitHub repo (`repo:owner/repo:ref:refs/heads/dev`).
3. Wire the 5 `AZURE_TS_*` repo secrets (no long-lived signing material).

## Local deploy

```bash
# Requires: signtool.exe in PATH + Azure CLI authenticated locally.
export SIGNTOOL="$(command -v signtool || echo 'C:/Program Files (x86)/Windows Kits/10/bin/x64/signtool.exe')"
TAG=v2026.06.04 bash deployment/desktop/msi-signed/script.sh
```

## Dev fallback

`dev_fallback.signing_mode: self-signed-cert` — local dev uses a developer-
generated self-signed cert (free, exercises the signing pipeline without
Azure costs).
