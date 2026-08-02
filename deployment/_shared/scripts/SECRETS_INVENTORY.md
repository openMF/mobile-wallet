<!-- deployment/_shared/scripts/SECRETS_INVENTORY.md -->
# gha_secret_var Inventory — mifos-x/kmp-project-template

Bridge between legacy GitHub Actions Secret names (preserved verbatim for
backward compatibility) and the new vault-managed aliases
(`core/registries/SECRETS_ALIAS_REGISTRY.yaml` → `kmp-project-template` group).

**Naming rule for NEW entries:** `<PLATFORM>_<NOUN>` (e.g. `CLOUDFLARE_API_TOKEN`).

Legacy names (`FIREBASECREDS`, `PLAYSTORECREDS`, `APPSTORE_KEY_ID`, …) are
PRESERVED even when they violate the new rule — renaming would break in-flight
workflows. New aliases reuse the legacy `env_var` so fastlane / GHA references
work unchanged once `/secrets pull` lands them.

| gha_secret_var (GH Secret name) | Alias (SECRETS_ALIAS_REGISTRY.yaml) | materialize.at | Owning capability | Legacy? |
|---|---|---|---|:-:|
| FIREBASECREDS | firebase_service_account_json | secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json | firebase-app-distribute | ✅ |
| PLAYSTORECREDS | play_publisher_service_account_json | secrets/live/android/play/playStorePublishServiceCredentialsFile.json | android-play-internal-publish | ✅ |
| APPSTORE_KEY_ID | appstore_key_id | secrets/apple/appstore/key_id | ios-app-store | ✅ |
| APPSTORE_ISSUER_ID | appstore_key_issuer_id | secrets/apple/appstore/issuer_id | ios-app-store | ✅ |
| APPSTORE_PRIVATE_KEY_P8 | appstore_private_key_p8 | secrets/apple/appstore/AuthKey.p8 | ios-app-store | ✅ |
| UPLOAD_KEYSTORE_FILE | kmp_template_release_keystore | secrets/android/keystores/upload_keystore.keystore | android-signing | ✅ |
| KEYSTORE_PASSWORD | upload_keystore_file_password | secrets/android/keystores/upload_keystore.properties | android-signing | ✅ |
| MATCH_GIT_PRIVATE_KEY | match_git_ssh_private_key | secrets/apple/match/match_ci_key | ios-signing | ✅ |
| MATCH_PASSWORD | match_password | secrets/apple/match/.match_password | ios-signing | ✅ |
| CLOUDFLARE_API_TOKEN | cloudflare_pages_api_token | secrets/web/cloudflare/api_token | web-cloudflare-pages-publish | — |
| CLOUDFLARE_ACCOUNT_ID | cloudflare_account_id | secrets/web/cloudflare/account_id | web-cloudflare-pages-publish | — |
| NETLIFY_AUTH_TOKEN | netlify_auth_token | secrets/web/netlify/auth_token | web-netlify-publish | — |
| NETLIFY_SITE_ID | netlify_site_id | secrets/web/netlify/site_id | web-netlify-publish | — |
| VERCEL_TOKEN | vercel_token | secrets/web/vercel/token | web-vercel-publish | — |
| VERCEL_ORG_ID | vercel_org_id | secrets/web/vercel/org_id | web-vercel-publish | — |
| VERCEL_PROJECT_ID | vercel_project_id | secrets/web/vercel/project_id | web-vercel-publish | — |

## Bridge contract

1. **Legacy GH Secret names are stable.** Fastlane lanes + GH Actions workflows
   reference `${{ secrets.FIREBASECREDS }}` etc. verbatim. Renaming = breakage.
2. **The vault is the source of truth.** `/secrets pull` decrypts each alias
   from the vault → writes to `materialize.at` → fastlane reads from the file.
3. **`/secrets sync-to-ci`** propagates each alias value back to the
   consumer-repo's GitHub Actions secrets under the legacy `gha_secret_var`
   name. This is the ONLY authorized path — `gh secret set` direct on the
   consumer repo is blocked by `core/scripts/gh-secret-set-guard.sh` (SV14).
4. **`keystore-manager.sh --manual-mode`** is a whitelisted bridge for the
   legacy Android keystore push flow (AC86). Audit-logged in
   `.claude-runtime/ci-state/secrets-audit.jsonl`.

## Authority

- RULE-SECRETS-VAULT-001 (SV14 — consumer-repo writers)
- RULE-SECRETS-ALIAS-REGISTRY-001 (SAR10 — every needs[].alias resolves)
- AC85 (inventory exists with ≥12 rows)
- PLAN-fastlane-modernization sub-plan 02 (T8, T9)
