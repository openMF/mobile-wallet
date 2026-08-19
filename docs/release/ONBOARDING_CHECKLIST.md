# Release Onboarding Checklist — blocked rungs

The multi-platform release chain is **fully wired**. The rungs below are blocked only because they
need real store/cert assets that cannot be generated in CI. Each is `skip`/inert by default so a
plain dispatch never dead-ends. Provide the asset via GitHub Secrets (never commit secrets to code —
CI reads them at runtime), then the rung lights up.

> All secrets live in GitHub Actions secrets (Settings → Secrets and variables → Actions). CI
> materializes them to ephemeral runner temp at runtime; nothing is stored in the repo.

## 🍎 macOS (TestFlight / Mac App Store) — needs `.p12` signing assets

Wired: `macos-signing` composite (keychain `.p12` import + Fastlane), 6 secrets forwarded across the
chain, `mac-testflight-internal/external` + `mac-app-store` environments (reviewer-gated).

To unblock, set these GitHub secrets on the consumer fork:
- [ ] `MAC_SIGNING_CERTIFICATE` — base64 of the Apple Distribution `.p12`
- [ ] `MAC_SIGNING_CERTIFICATE_PASSWORD`
- [ ] `MAC_INSTALLER_CERTIFICATE` — base64 of the Mac Installer Distribution `.p12`
- [ ] `MAC_INSTALLER_CERTIFICATE_PASSWORD`
- [ ] `MAC_PROVISIONING_PROFILE_BASE64` — base64 of the macOS App Store `.provisionprofile`
- [ ] `KEYCHAIN_PASSWORD` — any throwaway password for the ephemeral CI keychain
- [ ] A macOS app record in App Store Connect (separate from iOS)
Then dispatch with `mac_rung: internal` (or higher).

## 🪟 Windows signed (MSI / Microsoft Store) — needs Azure Trusted Signing

Wired: 8 `azure_*` secrets forwarded to `desktop-win`; the desktop publish repo validates them.
**Note:** `windows-exe` is NOT unsigned in this chain — it requires the same `azure_*` set as
`windows-msi-signed`. Only `linux-deb` is secret-free. Windows defaults to `skip`.

To unblock, set:
- [ ] `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`
- [ ] `AZURE_TRUSTED_SIGNING_ENDPOINT`, `AZURE_TRUSTED_SIGNING_ACCOUNT`, `AZURE_CERT_PROFILE_NAME`
- [ ] (Microsoft Store only) `MICROSOFT_STORE_CLIENT_ID`, `MICROSOFT_STORE_CLIENT_SECRET`
Then dispatch with `desktop_win_rung: prerelease` (or higher).

## 🤖 Android Play promotion (internal → beta → production) — needs Play Console

Wired: `PLAYSTORE_CREDS` forwarded; `android-play-internal/beta/production` environments
(reviewer-gated → the "Approve and deploy" promote button appears).

To unblock:
- [ ] Publish the app to the Play Console **internal track at least once** (the API rejects promotion
      from an empty track)
- [ ] Ensure the `PLAYSTORE_CREDS` service account has **Release Manager** permission
Then dispatch with `android_rung: internal` (or `firebase+internal`); approve each gate to promote.

## 🍏 iOS TestFlight / App Store — needs ASC app record + Match

Wired: `APPSTORE_*` + `MATCH_*` secrets forwarded; `ios-testflight-internal/external` + `ios-app-store`
environments (reviewer-gated). iOS **Firebase** works today (dev distribution).

To unblock TestFlight/App Store:
- [ ] An iOS app record in App Store Connect for the bundle id
- [ ] A Fastlane **Match** repo populated with `appstore` certificates + profiles, reachable via
      `MATCH_SSH_PRIVATE_KEY` + `MATCH_PASSWORD`
Then dispatch with `ios_rung: internal` (or higher); approve each gate.

---

**Deployable today (no onboarding):** Android Firebase, iOS Firebase (if Match exists), Linux DEB,
Web gh-pages. **Promote gates** (store/prod rungs) pause for maintainer approval by design.
