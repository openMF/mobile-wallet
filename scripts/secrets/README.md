# Platform-wise secrets toolkit

Self-contained setup for the secrets this project needs to build & release —
**android · apple · web · desktop**. Pure bash; no framework, no vault, no `yq`.
Fork the repo, run these, you're signed.

```bash
scripts/secrets/secrets-status.sh            # what's set up vs missing (all platforms)
scripts/secrets/secrets-status.sh apple      # one platform
scripts/secrets/setup-secrets.sh apple       # interactive: fill the missing apple secrets
scripts/secrets/setup-secrets.sh all         # walk every platform
scripts/secrets/generate-manifest.sh         # regenerate the root secrets-manifest.yaml
```

## How it works

The **source of truth** is the per-target need declarations:

```
deployment/<platform>/<target>/secrets-needs.yaml
  manual_inputs[]:   canonical (where it lands) · source_hint (how to get it) · gha_secret_var
  vault_aliases[]:   alias (org-level name) · canonical
```

- `secrets-status.sh` reads every `manual_inputs[].canonical` and checks it on disk.
- `setup-secrets.sh` walks the **missing** ones, prints `source_hint`, and stores what you
  provide at the right `secrets/<platform>/…` path. Passwords use hidden input; files are
  copied from a path you give — **values are never typed on the command line or echoed back**.
- `generate-manifest.sh` aggregates `vault_aliases[]` into the root `secrets-manifest.yaml`
  (don't hand-edit that file — edit the needs files and regenerate).

`deployment/_shared/config.rb` resolves every secret `ENV → secrets/<platform>/ file →
gradle/fork.properties → default`, so once the files are in place, `fastlane` just works.

## Platform → what you'll be asked for

| Platform | Secrets (examples) | Where to get them |
|---|---|---|
| **android** | upload keystore, Play SA json, Firebase SA json | Play Console · Firebase Console |
| **apple** | App Store Connect `.p8`, Match SSH key + password | App Store Connect → Keys · your Match repo |
| **web** | Vercel / Cloudflare / Netlify tokens | the host's dashboard |
| **desktop** | Windows (Azure Trusted Signing), macOS `.p12`, Linux GPG | per-store signing setup |

Non-secret identity (team id, Match git URL, app ids, store copy) lives in
**`gradle/fork.properties`** (copy `gradle/fork.properties.template`), *not* here.

## Framework users (Mifos maintainers)

If you work inside the Claude Product Cycle framework, you don't fill these by hand — the
vault has them. From a project-bound session:

```
/secrets-source-map        # maps these needs → org vault: what's on-disk / in-vault / must-request
/secrets pull              # materialize everything the vault already has
```

The framework reads these same `secrets-needs.yaml` files **one way** (it never writes the
source's connection back) — so the public fork stays standalone.
