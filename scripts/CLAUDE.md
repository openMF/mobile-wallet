# Bash Scripts - Automation & Setup

**Last Updated:** 2026-02-13
**Total Scripts:** 17
**Categories:** Setup (4) | iOS Deployment (3) | iOS Setup (2) | Verification (3) | Utilities (5)

[← Back to Main](../CLAUDE.md)

---

## Table of Contents

1. [Overview](#overview)
2. [Setup Scripts](#setup-scripts)
3. [iOS Deployment Scripts](#ios-deployment-scripts)
4. [iOS Setup Scripts](#ios-setup-scripts)
5. [Verification Scripts](#verification-scripts)
6. [Utility Scripts](#utility-scripts)
7. [Common Tasks](#common-tasks)
8. [Troubleshooting](#troubleshooting)

---

## Overview

This directory contains 17 bash scripts for project setup, deployment, and verification.

**Script Categories:**
```
setup-project.sh                  (565 lines) - thin redirect → scripts/white-label/doctor.sh (--legacy for old flow)

scripts/white-label/             - the white-label lifecycle (see scripts/white-label/README.md)
├── doctor.sh                     - the ONE entry: setup + verify + sync (RULE-WHITE-LABEL-DOCTOR-001)
├── derive.rb                     - app-profile → gradle/fork.properties (single SoT)
├── customize.sh                  (566 lines) - Package name customization  (was customizer.sh)
├── firebase.sh                   (469 lines) - Firebase project setup      (was firebase-setup.sh)
├── keystore.sh                 (1,356 lines) - Keystore & secrets mgmt      (was keystore-manager.sh)
└── sync-dirs.sh                  - template sync engine (owner: template, self-propagating)

ios-deployment/
├── deploy_firebase.sh            - iOS Firebase deployment
├── deploy_testflight.sh          - iOS TestFlight deployment
└── deploy_appstore.sh            - iOS App Store deployment

ios-setup/
├── setup_ios_complete.sh         (518 lines) - Complete iOS setup wizard
└── setup_apn_key.sh              - APN key configuration

verification/
├── verify_ios_deployment.sh      (365 lines, 70+ checks)
├── verify_apn_setup.sh           - APN verification
└── check_ios_version.sh          - Version sanitization display

utilities/
├── check_environment.sh          - Environment validation
├── check_file_env_keys.sh        - Environment file validation
├── check-commit-signing.sh       - Git commit signing check
├── ensure_base64.sh              - Base64 encoding helper
└── fix-detekt-permissions.sh     - Detekt config permissions fix
```

---

## Setup Scripts

### 1. `setup-project.sh` (565 lines)

**Purpose:** Master orchestration script for complete project setup

**Usage:**
```bash
./setup-project.sh
```

**What it does:**
1. **Validates Prerequisites:**
   - Checks for: `git`, `gh` (GitHub CLI), `firebase`, `keytool`, `openssl`, `python3`
   - Verifies: Git repository initialized, clean working directory

2. **Collects User Inputs:**
   - Package names (Android, iOS)
   - Organization info (name, department, city, country)
   - Firebase project info
   - Keystore passwords

3. **Orchestrates Setup:**
   - Runs `scripts/white-label/customize.sh` to update package names
   - Runs `scripts/white-label/firebase.sh` to create Firebase projects
   - Runs `scripts/white-label/keystore.sh` to generate keystores
   - Runs iOS setup (if user confirms)

4. **Generates Documentation:**
   - Creates `PROJECT_SETUP_INFO.txt` with all configuration details
   - Saves to project root

**Interactive Prompts:**
- Android package name
- iOS bundle identifier
- Organization details
- Firebase configuration
- iOS setup confirmation

**Output:**
- Updated package names across project
- Firebase projects configured
- Keystores generated
- Secrets encoded for GitHub Actions
- `PROJECT_SETUP_INFO.txt` documentation

**Line Count:** 565 lines

---

### 2. `scripts/white-label/customize.sh` (566 lines)

**Purpose:** Update package names and namespaces throughout the project

**Usage:**
```bash
scripts/white-label/customize.sh
```

**What it does:**
1. **Collects Package Info:**
   - Android package name (e.g., `com.example.app`)
   - iOS bundle identifier (e.g., `com.example.app`)

2. **Updates Android Configuration:**
   - `build.gradle.kts`: `namespace`, `applicationId`
   - `fastlane-config/project_config.rb`: ANDROID hash

3. **Updates iOS Configuration:**
   - Xcode project: `PRODUCT_BUNDLE_IDENTIFIER`
   - `fastlane-config/project_config.rb`: IOS hash
   - **Preserves** `IOS_SHARED` section (critical!)

4. **Creates google-services.json:**
   - Generates template with 4 variants:
     - `release`, `debug`, `demo`, `demo.debug`
   - Placeholder app IDs (will be updated by `scripts/white-label/firebase.sh`)

5. **Updates libs.versions.toml:**
   - `androidPackageNamespace` version

**⚠️ IMPORTANT:** Run `scripts/white-label/firebase.sh` after this to populate real Firebase app IDs

**Interactive:** Yes (prompts for package names)

**Line Count:** 566 lines

---

### 3. `scripts/white-label/firebase.sh` (469 lines)

**Purpose:** Create Firebase projects and register Android/iOS apps

**Usage:**
```bash
scripts/white-label/firebase.sh
```

**What it does:**
1. **Creates Firebase Project:**
   - Project ID from Android package name
   - Adds Firebase Admin SDK

2. **Registers Apps (2 Android + 1 iOS):**
   - Android Prod: `{package}.prod` (SHA-1 certificate)
   - Android Demo: `{package}.demo.debug` (SHA-1 certificate)
   - iOS: `{bundle_id}` (no SHA required)

3. **Downloads Configurations:**
   - `google-services.json` for each Android app
   - `GoogleService-Info.plist` for iOS

4. **Merges Configurations:**
   - Uses Python script to merge 2 Android configs into 4-variant `google-services.json`
   - **Optimization:** Only 2 apps registered in Firebase (saves quota), but 4 variants in config

5. **Saves to Correct Locations:**
   - `cmp-android/google-services.json`
   - `cmp-ios/GoogleService-Info.plist`

**Prerequisites:**
- `firebase` CLI installed and logged in
- `python3` installed
- Android keystore exists (for SHA-1 certificate)

**Variant Mapping:**
```
Firebase App               → Config Variant
---------------------------------------------
{package}.prod            → release, prod
{package}.demo.debug      → debug, demo, demo.debug
```

**Interactive:** Yes (project name, confirmation)

**Line Count:** 469 lines

**⚠️ Note:** Only 2 Firebase apps created, but 4 variants in google-services.json

---

### 4. `scripts/white-label/keystore.sh` (1,356 lines)

**Purpose:** Complete keystore and secrets management tool

**Usage:**
```bash
scripts/white-label/keystore.sh <command>
```

**Commands:**

#### `generate`
Generate ORIGINAL and UPLOAD keystores

```bash
scripts/white-label/keystore.sh generate
```

**What it does:**
1. Validates `keytool` and `openssl` installed
2. Loads configuration from `secrets.env` (or prompts for input)
3. Generates **ORIGINAL keystore**:
   - File: `keystores/{package}_original.keystore`
   - Used for app signing
4. Generates **UPLOAD keystore**:
   - File: `keystores/{package}_upload.keystore`
   - Used for Play Console (Play App Signing)
5. Extracts certificates and keys
6. Saves configuration to `secrets.env`

**Interactive:** Yes (if no secrets.env)

---

#### `encode-secrets`
Encode all secrets in `secrets/` directory to base64

```bash
scripts/white-label/keystore.sh encode-secrets
```

**What it does:**
1. Scans `secrets/` directory for files
2. Encodes each file to base64
3. Updates `secrets.env` with encoded file secrets

**File-to-Secret Mapping:**
```bash
FILE_TO_SECRET_MAP["firebaseAppDistributionServiceCredentialsFile.json"]="FIREBASECREDS"
FILE_TO_SECRET_MAP["google-services.json"]="GOOGLESERVICES"
FILE_TO_SECRET_MAP["playStorePublishServiceCredentialsFile.json"]="PLAYSTORECREDS"
FILE_TO_SECRET_MAP["AuthKey.p8"]="APPSTORE_AUTH_KEY"
FILE_TO_SECRET_MAP["match_ci_key"]="MATCH_SSH_PRIVATE_KEY"
```

---

#### `sync`
Synchronize all secrets to secrets.env

```bash
scripts/white-label/keystore.sh sync
```

**What it does:**
1. Read non-secret metadata from `gradle/fork.properties`
2. Encode `secrets/*` files to base64
3. Update `secrets.env` with all platform secrets
4. Add Desktop signing placeholders
5. Validate result (format, required secrets, base64)

**Output Artifacts:**
- Updated `secrets.env` (all platform secrets unified)
- `secrets.env.backup` (safety backup)

**Use cases:**
- After iOS setup (automatic via `setup_ios_complete.sh`)
- After adding new files to `secrets/` directory
- After updating `gradle/fork.properties` or any `secrets/<platform>/` file
- To refresh/validate secrets.env

**Features:**
- Creates backup before modification
- Preserves existing secrets if source files missing
- Idempotent: safe to run multiple times
- Validates format and base64 encoding

---

#### `view`
View current secrets (files + environment variables)

```bash
scripts/white-label/keystore.sh view
```

**What it does:**
- Lists all files in `secrets/` directory
- Displays selected environment variables from `secrets.env`
- Shows keystore info (aliases, validity)

---

#### `add`
Add secrets to GitHub repository (requires `gh` CLI)

```bash
scripts/white-label/keystore.sh add [--repo owner/repo] [--env environment]
```

**What it does:**
1. Encodes all secrets to base64
2. Uses GitHub CLI to set repository secrets:
   ```bash
   gh secret set SECRET_NAME --body "$base64_value"
   ```
3. Optionally targets specific environment

**Prerequisites:** `gh auth login` (GitHub CLI authenticated)

---

#### `list`
List all secrets in GitHub repository

```bash
scripts/white-label/keystore.sh list [--repo owner/repo]
```

**Uses:** `gh secret list`

---

#### `delete`
Delete a specific secret from GitHub

```bash
scripts/white-label/keystore.sh delete [--repo owner/repo] [--name SECRET_NAME]
```

**Interactive:** Prompts for secret name if not provided

---

#### `delete-all`
Delete ALL secrets from GitHub repository

```bash
scripts/white-label/keystore.sh delete-all [--repo owner/repo]
```

**⚠️ WARNING:** Irreversible! Requires confirmation.

---

**Configuration File: `secrets.env`**

Example:
```bash
# Company & Organization
COMPANY_NAME="Your Company"
DEPARTMENT="Mobile Development"
ORGANIZATION="Your Org"
CITY="San Francisco"
STATE="CA"
COUNTRY="US"

# Keystore Configuration
VALIDITY=10000
KEYALG=RSA
KEYSIZE=2048

# Keystore Credentials (Play App Signing — single UPLOAD keystore)
UPLOAD_KEYSTORE_PASSWORD="xxx"
UPLOAD_KEYSTORE_ALIAS="xxx"
UPLOAD_KEYSTORE_ALIAS_PASSWORD="xxx"
```

**Line Count:** 1,356 lines (largest script)

---

## iOS Deployment Scripts

### 5. `deploy_firebase.sh`

**Purpose:** Deploy iOS app to Firebase App Distribution

**Usage:**
```bash
./scripts/deploy/deploy_firebase.sh
```

**What it does:**
1. Reads config from `gradle/fork.properties` + `secrets/apple/` files
2. Calls Fastlane lane: `bundle exec fastlane ios deploy_on_firebase`
3. Displays success message

**Prerequisites:**
- `gradle/fork.properties` filled in and `secrets/apple/` files present
- Fastlane installed
- CocoaPods installed

**Output:** IPA uploaded to Firebase App Distribution

---

### 6. `deploy_testflight.sh`

**Purpose:** Deploy iOS app to TestFlight

**Usage:**
```bash
./scripts/deploy/deploy_testflight.sh
```

**What it does:**
1. Reads config from `gradle/fork.properties` + `secrets/apple/` files
2. Calls Fastlane lane: `bundle exec fastlane ios beta`
3. Displays success message

**Prerequisites:** Same as `deploy_firebase.sh`

**Output:** IPA uploaded to TestFlight

---

### 7. `deploy_appstore.sh`

**Purpose:** Deploy iOS app to App Store

**Usage:**
```bash
./scripts/deploy/deploy_appstore.sh
```

**What it does:**
1. **Double Confirmation:**
   - First confirmation: "Deploy to App Store? (y/n)"
   - Second confirmation: "This is PRODUCTION. Are you ABSOLUTELY sure? (yes/no)"
   - Requires typing "yes" (not just "y")
2. Reads config from `gradle/fork.properties` + `secrets/apple/` files
3. Calls Fastlane lane: `bundle exec fastlane ios release`
4. Displays success message

**⚠️ CRITICAL:**
- Production deployment
- Double confirmation required
- Never bypass this script

**Prerequisites:** Same as `deploy_firebase.sh`

**Output:** IPA submitted to App Store for review

---

## iOS Setup Scripts

### 8. `setup_ios_complete.sh` (518 lines)

**Purpose:** Complete iOS deployment setup wizard

**Usage:**
```bash
./scripts/ios/setup_ios_complete.sh
```

**What it does:**

1. **Validates Prerequisites:**
   - Checks: `openssl`, `ssh-keygen`, `gh` CLI, Xcode Command Line Tools

2. **Collects Configuration:**
   - **Team Information:**
     - Apple Developer Team ID
     - Bundle Identifier
   - **App Store Connect API:**
     - Key ID
     - Issuer ID
     - Private Key (.p8 file path)
   - **Fastlane Match:**
     - Git repository URL
     - Git branch
     - Passphrase

3. **Generates SSH Key for Match:**
   - Creates `secrets/apple/match/match_ci_key` (private key)
   - Creates `secrets/apple/match/match_ci_key.pub` (public key)
   - Instructions to add public key to Match repository deploy keys

4. **Writes configuration:**
   - Populates `gradle/fork.properties` with non-secret identity/metadata:
     ```properties
     apple.team.id=xxx
     apple.match.git.url=git@github.com:org/repo.git
     apple.match.git.branch=master
     org.email=team@example.com
     ```
   - Writes secret values as per-value files under `secrets/<platform>/`:
     - `secrets/apple/appstore/key_id`, `issuer_id`
     - `secrets/apple/match/.match_password`

5. **Copies App Store Connect Key:**
   - Copies `.p8` file to `secrets/apple/appstore/AuthKey.p8`

6. **Verifies Configuration:**
   - Runs `verify_ios_deployment.sh` (70+ checks)

7. **Displays Next Steps:**
   - Add SSH public key to Match repository
   - Run `fastlane match` to generate certificates
   - Test deployment with `deploy_firebase.sh`

**Interactive:** Extensive (prompts for all configuration)

**Line Count:** 518 lines

**Output:**
- `gradle/fork.properties` (non-secret identity/metadata)
- `secrets/apple/appstore/key_id` and `secrets/apple/appstore/issuer_id`
- `secrets/apple/match/match_ci_key` (SSH private key)
- `secrets/apple/match/match_ci_key.pub` (SSH public key)
- `secrets/apple/appstore/AuthKey.p8` (App Store Connect API key)

---

### 9. `setup_apn_key.sh`

**Purpose:** Configure Apple Push Notification (APN) key

**Usage:**
```bash
./scripts/ios/setup_apn_key.sh
```

**What it does:**
1. Prompts for APN Key ID
2. Prompts for APN .p8 file path
3. Copies APN key to `secrets/apple/apn/APNAuthKey.p8`
4. Writes APN Key ID to `secrets/apple/apn/key_id` and Team ID to `secrets/apple/apn/team_id`
5. Verifies setup with `verify_apn_setup.sh`

**When needed:**
- If app uses push notifications
- Optional for apps without notifications

---

## Verification Scripts

### 10. `verify_ios_deployment.sh` (365 lines, 70+ checks)

**Purpose:** Comprehensive validation of iOS deployment configuration

**Usage:**
```bash
./scripts/ios/verify_ios_deployment.sh
```

**What it does:**

**70+ Checks across 10 categories:**

1. **Prerequisites (7 checks):**
   - Ruby installed
   - Bundler installed
   - Fastlane installed
   - CocoaPods installed
   - Xcode Command Line Tools
   - Git installed
   - OpenSSL installed

2. **Fastlane Configuration (8 checks):**
   - `Gemfile` exists
   - `Fastfile` exists
   - iOS platform defined in Fastfile
   - Required lanes exist (`beta`, `release`, `deploy_on_firebase`)
   - Plugins installed (`firebase_app_distribution`, `increment_build_number`)

3. **Project Structure (6 checks):**
   - Xcode project exists
   - Workspace exists
   - Shared module exists
   - Podfile exists
   - Info.plist exists

4. **App Store Connect API (4 checks):**
   - `APPSTORE_KEY_ID` set
   - `APPSTORE_ISSUER_ID` set
   - `APPSTORE_KEY_PATH` set
   - `.p8` file exists at path

5. **Fastlane Match (6 checks):**
   - `MATCH_GIT_URL` set
   - `MATCH_GIT_BRANCH` set
   - `MATCH_SSH_KEY_PATH` set
   - SSH private key exists
   - `MATCH_PASSWORD` set
   - SSH key has correct permissions (600)

6. **Firebase Configuration (5 checks):**
   - `GoogleService-Info.plist` exists
   - Firebase service credentials exist
   - Firebase app ID configured
   - Firebase tester groups configured

7. **Code Signing (10 checks):**
   - Team ID set
   - Bundle identifier set
   - Provisioning profile name format
   - Match type configured
   - SSH config for Match repository
   - Can connect to Match repository
   - Certificates exist in Match repository

8. **Build Configuration (8 checks):**
   - Scheme exists
   - Build settings valid
   - Code signing identity set
   - Provisioning profile set
   - Development team set
   - Version number set
   - Build number set

9. **Deployment Readiness (6 checks):**
   - Can build debug
   - Can build release (dry run)
   - Can access App Store Connect API
   - Can fetch Match certificates (dry run)
   - Firebase credentials valid

10. **Security (5 checks):**
    - Secrets directory exists
    - Secret files have correct permissions
    - No secrets committed to Git
    - SSH key not world-readable
    - `.p8` file not world-readable

**Output:**
- ✅ All checks passed → "iOS deployment configuration is READY"
- ❌ Some checks failed → Detailed error messages with fixes

**Line Count:** 365 lines

**Exit Codes:**
- `0`: All checks passed
- `1`: One or more checks failed

---

### 11. `verify_apn_setup.sh`

**Purpose:** Verify Apple Push Notification setup

**Usage:**
```bash
./scripts/ios/verify_apn_setup.sh
```

**What it does:**
1. Checks `secrets/apple/apn/key_id` exists and is non-empty
2. Checks APN `.p8` file exists at `secrets/apple/apn/APNAuthKey.p8`
3. Validates file permissions
4. Verifies key format

**Output:**
- ✅ APN setup valid
- ❌ Missing configuration or invalid key

---

### 12. `check_ios_version.sh`

**Purpose:** Display version handling and sanitization explanation

**Usage:**
```bash
./scripts/ios/check_ios_version.sh
```

**What it does:**
1. Reads current version from `version.txt` (generated by Gradle)
2. Shows version formats:
   ```
   Gradle Version:    2026.1.1-beta.0.9+abc123
   Firebase Version:  2026.1.1-beta.0.9
   App Store Version: 2026.1.9
   ```
3. Explains sanitization logic:
   - Why: App Store requires `MAJOR.MINOR.PATCH` format
   - How: Extract year, month, commit count
   - Result: `YYYY.M.{commitCount}`
4. Shows Fastlane code that performs sanitization (from Fastfile:383-429)

**Educational Output:**
- Version comparison table
- Sanitization explanation
- Code snippet from Fastfile

See [Version Handling Guide](../docs/claude/version-handling.md)

---

## Utility Scripts

### 13. `check_environment.sh`

**Purpose:** Validate development environment prerequisites

**Usage:**
```bash
./scripts/check_environment.sh
```

**What it does:**
- Checks for required tools:
  - Git
  - Java (version 17+)
  - Gradle
  - Ruby
  - Bundler
  - CocoaPods (if macOS)
  - Firebase CLI
  - GitHub CLI (`gh`)
- Displays versions
- Reports missing tools

**Output:**
- ✅ All prerequisites met
- ⚠️ Missing tools (with installation instructions)

---

### 14. `check_file_env_keys.sh`

**Purpose:** Validate environment variable files

**Usage:**
```bash
./scripts/check_file_env_keys.sh <file.env>
```

**What it does:**
1. Reads `.env` file
2. Checks for required keys
3. Validates format (KEY=VALUE)
4. Detects multiline blocks
5. Reports missing or malformed entries

**Use cases:**
- Validate `secrets.env` after manual edits
- Check any `.env`-formatted file format

---

### 15. `check-commit-signing.sh`

**Purpose:** Check if Git commits are GPG-signed

**Usage:**
```bash
./scripts/check-commit-signing.sh
```

**What it does:**
- Checks Git configuration for GPG signing
- Verifies GPG key exists
- Validates signing setup
- Provides setup instructions if not configured

**Output:**
- ✅ Commit signing configured
- ℹ️ Setup instructions

---

### 16. `ensure_base64.sh`

**Purpose:** Helper to encode files to base64

**Usage:**
```bash
./scripts/ensure_base64.sh <file>
```

**What it does:**
- Encodes file to base64
- Handles platform differences (macOS vs Linux)
- Outputs to stdout or file

**Use cases:**
- Manually encode secrets for GitHub Actions
- Quick base64 encoding

---

### 17. `fix-detekt-permissions.sh`

**Purpose:** Fix Detekt configuration file permissions

**Usage:**
```bash
./scripts/fix-detekt-permissions.sh
```

**What it does:**
- Finds Detekt config files (`detekt.yml`, `detekt-config.yml`)
- Sets correct permissions (644)
- Fixes "Permission denied" errors

**When needed:**
- After cloning repository
- If Detekt fails with permission errors

---

## Common Tasks

### Initial Project Setup

```bash
# Complete setup (interactive)
./setup-project.sh

# OR step-by-step:

# 1. Customize package names
scripts/white-label/customize.sh

# 2. Setup Firebase
scripts/white-label/firebase.sh

# 3. Generate keystores
scripts/white-label/keystore.sh generate

# 4. Encode secrets for GitHub Actions
scripts/white-label/keystore.sh encode-secrets

# 5. Add secrets to GitHub (requires gh CLI)
scripts/white-label/keystore.sh add

# 6. Setup iOS (if needed)
./scripts/ios/setup_ios_complete.sh
```

---

### iOS Deployment

```bash
# Deploy to Firebase
./scripts/deploy/deploy_firebase.sh

# Deploy to TestFlight
./scripts/deploy/deploy_testflight.sh

# Deploy to App Store (double confirmation required)
./scripts/deploy/deploy_appstore.sh
```

---

### Secrets Management

```bash
# Synchronize all secrets to secrets.env (recommended)
scripts/white-label/keystore.sh sync

# View current secrets
scripts/white-label/keystore.sh view

# Encode all secrets
scripts/white-label/keystore.sh encode-secrets

# Add secrets to GitHub
scripts/white-label/keystore.sh add

# List GitHub secrets
scripts/white-label/keystore.sh list

# Delete a secret
scripts/white-label/keystore.sh delete --name SECRET_NAME

# Delete all secrets (with confirmation)
scripts/white-label/keystore.sh delete-all
```

---

### Verification

```bash
# Check environment prerequisites
./scripts/check_environment.sh

# Verify iOS deployment setup
./scripts/ios/verify_ios_deployment.sh

# Check version sanitization
./scripts/ios/check_ios_version.sh

# Validate environment file
./scripts/check_file_env_keys.sh secrets.env
```

---

## Troubleshooting

### Setup Issues

#### 1. `setup-project.sh` fails with "Command not found"

**Cause:** Missing prerequisite tools

**Fix:**
```bash
# Check what's missing
./scripts/check_environment.sh

# Install missing tools (macOS)
brew install git gh firebase-cli
```

---

#### 2. `scripts/white-label/customize.sh` doesn't update package names

**Cause:** Script requires interactive input

**Fix:**
- Run script in terminal (not via IDE)
- Provide valid package name (e.g., `com.example.app`)

---

#### 3. `scripts/white-label/firebase.sh` fails with "Authentication error"

**Cause:** Not logged into Firebase CLI

**Fix:**
```bash
firebase login
```

---

### Keystore Issues

#### 4. `scripts/white-label/keystore.sh generate` fails

**Cause:** Missing `keytool` or `openssl`

**Fix:**
```bash
# Check if Java is installed
java -version

# Install if missing (macOS)
brew install openjdk@17

# Check OpenSSL
openssl version
brew install openssl  # if missing
```

---

#### 5. Secrets not added to GitHub

**Cause:** GitHub CLI not authenticated

**Fix:**
```bash
gh auth login
gh auth status  # Verify

# Then retry
scripts/white-label/keystore.sh add
```

---

### iOS Setup Issues

#### 6. `setup_ios_complete.sh` can't generate SSH key

**Cause:** Missing `ssh-keygen`

**Fix:**
```bash
# macOS (should be installed by default)
xcode-select --install

# Verify
ssh-keygen -V
```

---

#### 7. Match repository access denied

**Cause:** SSH public key not added to repository

**Fix:**
1. Get public key:
   ```bash
   cat secrets/apple/match/match_ci_key.pub
   ```
2. Go to Match repository → Settings → Deploy keys
3. Add public key with write access

---

### Deployment Issues

#### 8. `deploy_firebase.sh` fails with "Secrets not found"

**Cause:** Required secret files missing under `secrets/apple/` or `gradle/fork.properties` not filled in.

**Fix:**
```bash
# Run iOS setup wizard
./scripts/ios/setup_ios_complete.sh

# OR manually create the required files:
# - gradle/fork.properties (from gradle/fork.properties.template)
# - secrets/apple/appstore/key_id, issuer_id, AuthKey.p8
# - secrets/apple/match/match_ci_key, .match_password
```

---

#### 9. `verify_ios_deployment.sh` reports failures

**Cause:** Incomplete iOS setup

**Fix:**
- Read failure messages carefully
- Each check has specific fix instructions
- Re-run setup wizard if needed:
  ```bash
  ./scripts/ios/setup_ios_complete.sh
  ```

---

### Version Issues

#### 10. Version mismatch between platforms

**Cause:** Gradle version not compatible with App Store

**Fix:**
- Fastlane automatically sanitizes versions
- Check with: `./scripts/ios/check_ios_version.sh`
- See [Version Handling Guide](../docs/claude/version-handling.md)

---

**Need more help?**
- [Deployment Playbook](../docs/claude/deployment-playbook.md)
- [Secrets Management Guide](../docs/claude/secrets-management.md)
- [Troubleshooting Guide](../docs/claude/troubleshooting.md)
- [Known Issues](../docs/analysis/BUGS_AND_ISSUES.md)

[← Back to Main](../CLAUDE.md)
