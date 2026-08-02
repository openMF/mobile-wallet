# deployment/desktop/microsoft-store — Microsoft Store

**Tier:** 2 (opt-in)
**Owning capability:** `desktop-microsoft-store`
**Signing:** Microsoft (Partner Center submission flow)
**Gating:** `requires_confirm: true`

## What this deploys

MSIX package submitted to the Microsoft Store via Partner Center. Packaging
runs `MakeAppx.exe pack` against the staged Compose Desktop app payload;
submission rides on `microsoft/StoreBroker-Action@v1`.

## Setup

> **Note:** all `ms_partner_center_*` + `ms_app_id` aliases are declared but
> rows are **pending in `SECRETS_ALIAS_REGISTRY`**. Add via `/secrets request
> <alias>` when Partner Center is bootstrapped for your org.

Required (one-time, per org):
1. Enroll in the Microsoft Partner Center (Windows Store dev account, $19 one-time).
2. Create an Azure AD App registration with API access to Partner Center.
3. Reserve your app name + capture the Store ID → `MS_APP_ID`.

## Local deploy

```bash
# Requires Windows + Windows 10 SDK (MakeAppx.exe) + PowerShell StoreBroker module.
export MAKEAPPX='C:/Program Files (x86)/Windows Kits/10/bin/10.0.22621.0/x64/MakeAppx.exe'
export MS_PARTNER_CENTER_TENANT_ID=...
export MS_PARTNER_CENTER_CLIENT_ID=...
export MS_PARTNER_CENTER_CLIENT_SECRET=...
export MS_APP_ID=...
bash deployment/desktop/microsoft-store/script.sh
# Then submit via PowerShell + StoreBroker
```

## Dev fallback

`dev_fallback.signing_mode: self-signed-cert` — local install with
`Add-AppxPackage -AllowUnsigned` from a developer-generated cert.
