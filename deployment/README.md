# deployment/ — kmp-project-template

Per-target deploy scaffold for every platform the template ships to.
Replaces the legacy monolithic `fastlane/` tree at project root (slated for
deletion in sub-plan 14 of the `fastlane-modernization` epic).

> **Status (2026-06-04, SP-06):** SKELETON ONLY. Per-target folders are empty
> stubs (`.gitkeep`) until sub-plans 07 (Android) / 08 (iOS) / 09 (desktop) /
> 10 (web) populate them. The contracts (schemas, manifest, rule) are live
> NOW so those sub-plans have something to validate against.

---

## Layout

```
deployment/
├── DEPLOYMENT_MANIFEST.yaml     ← enumerates every target (schema v1.0)
├── PROMOTION_LOG.yaml           ← append-only deploy audit log (12-field)
├── Fastfile                     ← canonical import delegator (NO lanes inline)
├── Appfile                      ← reads identity from FastlaneConfig (config.rb)
├── README.md                    ← (this file)
├── _shared/
│   ├── lib/                     ← Ruby helpers (populated in SP-07/08)
│   ├── scripts/
│   │   ├── _README.md           ← script index — purpose/inputs/outputs/exit
│   │   ├── scripts/white-label/keystore.sh  ← Android keystore lifecycle (moved by SP-05)
│   │   └── SECRETS_INVENTORY.md ← secrets reference doc (from SP-02)
│   └── secrets-needs-schema.yaml ← human-readable dual-mode schema cheat-sheet
├── android/                     ← Tier-1 + Tier-2 Android targets
│   ├── firebase/                ← Firebase App Distribution (Tier 1, default ON)
│   ├── play-internal/           ← Play Console Internal Testing (Tier 1, default ON)
│   ├── play-beta/               ← Play Console Closed Testing — Beta (Tier 1, default ON)
│   └── play-production/         ← Play Console Production (Tier 1, default OFF, requires_confirm)
├── ios/                         ← Tier-1 iOS targets
│   ├── firebase/                ← Firebase App Distribution (Tier 1, default ON)
│   ├── testflight/              ← TestFlight (Tier 1, default ON)
│   └── appstore/                ← App Store (Tier 1, default OFF, requires_confirm)
├── desktop/                     ← Tier-1 unsigned + Tier-2 signed/stores
│   ├── linux-deb/               ← Tier 1 .deb (default ON)
│   ├── windows-exe/             ← Tier 1 .exe (default ON)
│   ├── macos-dmg-unsigned/      ← Tier 1 unsigned .dmg (default ON)
│   ├── dmg-notarized/           ← Tier 2 notarized .dmg (default OFF)
│   ├── msi-signed/              ← Tier 2 signed .msi (default OFF)
│   ├── mac-app-store/           ← Tier 2 Mac App Store (default OFF)
│   └── microsoft-store/         ← Tier 2 Microsoft Store (default OFF)
└── web/                         ← Tier-1 GH Pages + Tier-2 hosting providers
    ├── gh-pages/                ← Tier 1 GitHub Pages (default ON)
    ├── cloudflare-pages/        ← Tier 2 Cloudflare Pages (default OFF)
    ├── netlify/                 ← Tier 2 Netlify (default OFF)
    └── vercel/                  ← Tier 2 Vercel (default OFF)
```

---

## Decision matrix — when to add a new target

| If you want to … | Then … |
|---|---|
| Add a NEW per-target Fastfile to an existing platform | Add row to `DEPLOYMENT_MANIFEST.yaml#enabled_targets[]`, create `deployment/<platform>/<target>/{Fastfile,config.yaml,secrets-needs.yaml,README.md}`, run validator. |
| Add a NEW platform (e.g. tvOS) | Bump `deployment-manifest.schema.json` platform enum + author per-platform helper module + add tier-A row(s). Schema change is a framework PR. |
| Disable a target temporarily | Flip `enabled: true` → `false` for that row in `DEPLOYMENT_MANIFEST.yaml`. |
| Require a secret that isn't in the vault yet | Add row to per-target `secrets-needs.yaml#manual_inputs[]` with `placeholder: secrets/sample/<path>`, drop a real placeholder file at that path, run `deployment/_shared/scripts/manual-preflight.sh`. |
| Promote a target from Tier-2 → Tier-1 | Update `tier` in the manifest, drop `requires_confirm`, document why in CHANGELOG. |

---

## Contract convergence

Both modes of secret resolution — vault alias and manual placeholder —
terminate at the same filesystem path: `secrets/<canonical>`. Fastlane
lanes and GHA workflows ONLY read from that path; they never branch on
the mode. This means a lane authored against vault-mode keeps working when
an operator drops to manual-mode for a single deploy, and vice versa.

```
[ vault ]               [ manual ]
   │                       │
   │ secrets pull --target │ secrets-init-from-demo.sh
   │                       │
   ▼                       ▼
   ───────► secrets/<canonical> ◄───────
                   │
                   ▼
            Fastlane / GHA reads
```

---

## Related

- `core/schemas/deployment-manifest.schema.json` — manifest structural schema
- `core/schemas/secrets-needs.schema.json` — per-target dual-mode schema
- `core/schemas/promotion-log.schema.json` — 12-field audit row schema
- `core/scripts/deployment-manifest-validate.sh` — validator
- `layers/release/rules/RULE-DEPLOYMENT-MANIFEST-001.md` — enforcement rule
- `plan-layer/project-plans/mifos-x/kmp-project-template/active/fastlane-modernization/GOAL.md` — epic spec
- `plan-layer/project-plans/mifos-x/kmp-project-template/active/fastlane-modernization/06-deployment-scaffold-and-manifest.md` — this sub-plan
