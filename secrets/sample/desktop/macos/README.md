# secrets/desktop/macos/ — macOS desktop (Compose Desktop) signing

Mac App Store / notarized-DMG signing for the **desktop** app. Apple-*account*
auth (ASC API key + Match) lives in `secrets/apple/` and is shared; the files
here are the macOS *packaging* certificates.

| File | GH secret | What |
|---|---|---|
| `app_store.p12` | `MAC_APP_STORE_CERT_B64` | 3rd Party Mac Developer Application cert (.p12) |
| `installer.p12` | `MAC_INSTALLER_CERT_B64` | 3rd Party Mac Developer Installer cert (.pkg signing) |
| `*.provisionprofile` | `MAC_*_PROVISION_B64` | macOS provisioning profiles (if MAS) |

Export from Keychain Access → My Certificates → right-click → Export (.p12).
Tier-3: onboarded when macOS distribution is enabled.
