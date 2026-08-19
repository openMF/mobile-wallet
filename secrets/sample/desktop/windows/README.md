# secrets/desktop/windows/ — Windows desktop code signing

| File | GH secret | What |
|---|---|---|
| `code_signing.pfx` | `WINDOWS_SIGNING_CERTIFICATE` | Authenticode code-signing cert (.pfx) |
| `code_signing_password` | `WINDOWS_SIGNING_PASSWORD` | .pfx password |

Modern alternative: **Azure Trusted Signing** (no local cert) — set the
`AZURE_TS_*` env vars in CI instead of a .pfx. Tier-3: onboarded when Windows signing is enabled.
