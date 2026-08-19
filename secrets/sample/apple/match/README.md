# Fastlane Match — iOS Code Signing

Used for: managing iOS certificates and provisioning profiles across team/CI.
Match stores them encrypted in a private Git repo and fetches on demand.

## How to set it up (first time)

### 1. Create a private repository for certificates

Create a **private** GitHub repo, e.g. `your-org/ios-certificates`.
It will store encrypted `.p12` files and `.mobileprovision` files.

### 2. Generate an SSH deploy key

```bash
mkdir -p secrets/apple/match
ssh-keygen -t ed25519 -C "fastlane-match-ci" -f secrets/apple/match/match_ci_key -N ""
# This creates:
#   secrets/apple/match/match_ci_key       (private — keep secret)
#   secrets/apple/match/match_ci_key.pub   (public — add to certificate repo)
```

### 3. Add the public key to the certificate repo

1. Go to your certificate repo on GitHub
2. Settings → Deploy keys → Add deploy key
3. Paste the contents of `secrets/apple/match/match_ci_key.pub`
4. Enable **Allow write access** (Match needs to push new certs)

### 4. Generate Match encryption password

```bash
openssl rand -base64 32 | tr -d '\n' > secrets/apple/match/.match_password
```

Store this password somewhere safe (password manager). Anyone with it + the
Git repo can decrypt your certificates.

### 5. Set the Match Git URL in fork.properties

```bash
cp gradle/fork.properties.template gradle/fork.properties
# Edit gradle/fork.properties and set:
#   apple.match.git.url=git@github.com:your-org/ios-certificates.git
#   apple.match.git.branch=main
```

### 6. Initialize Match (first fork only)

```bash
MATCH_GIT_URL=$(grep '^apple.match.git.url' gradle/fork.properties | cut -d= -f2)
export MATCH_PASSWORD=$(cat secrets/apple/match/.match_password)
bundle exec fastlane match init \
  --git_url "$MATCH_GIT_URL" \
  --type appstore
```

## Files to populate

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/apple/match/match_ci_key` | SSH private key | `MATCH_GIT_PRIVATE_KEY` |
| `secrets/apple/match/match_ci_key.pub` | SSH public key (add to cert repo) | — |
| `secrets/apple/match/.match_password` | Match encryption passphrase | `MATCH_PASSWORD` |

## Sync to GitHub

```bash
bash scripts/secrets/sync-secrets-to-github.sh --only ios
```

## Notes

- Match supports `appstore`, `adhoc`, and `development` certificate types
- CI always runs in `readonly` mode — only your local machine can refresh certs
- Rotate the encryption password by re-encrypting all certs: `bundle exec fastlane match nuke`
