# secrets/desktop/linux/ — Linux package signing

| File | GH secret | What |
|---|---|---|
| `gpg_signing.key` | `LINUX_SIGNING_KEY` | GPG private key (ASCII-armored) for .deb/.rpm signing |
| `gpg_passphrase` | `LINUX_SIGNING_PASSWORD` | GPG key passphrase |

Generate: `gpg --full-generate-key` → `gpg --armor --export-secret-keys <id>`.
Tier-3: onboarded when Linux signed packages are enabled.
