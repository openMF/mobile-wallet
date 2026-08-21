# `core-base/security`

Framework-shared crypto / session / tamper-detection primitives — brand-neutral, no domain logic. There
is no sibling `core/security` wrapper; a fork configures and binds directly here via `SecurityConfig` /
`SecurityPolicy` and the auto-included `SecurityModule` Koin module.

## What's here

- **Key + field crypto** (`expect`/`actual` per platform): `SecureKeyProvider` (Android Keystore / iOS
  Keychain Services / desktop OS credential APIs / web IndexedDB CryptoKey), `FieldEncryptor`
  (AES-256-GCM, encrypts individual fields before Room/Settings persistence), `SecureRandom`,
  `SecureWiper`.
- **Session + auth orchestration**: `SessionManager` (inactivity timeout), `FailedAttemptTracker`
  (lockout/wipe thresholds), `SecureAuthManager` (coordinates both plus `BiometricAuthenticator`),
  `SecurityConfig` / `SecurityPolicy` (the tunable knobs, defaulted so the template runs unconfigured).
- **Runtime wiring**: `SecurityGate` — a root composable that auto-wires startup tamper detection,
  lifecycle-driven session-timeout checks, pointer-input session touch, and biometric re-auth on
  session expiry, exposing `LocalSecurityState` / `SecurityState` to the composition tree.
- **Tamper + integrity**: `TamperDetector` — root/jailbreak, debugger-attached, and signature checks.
  Results are advisory; the consumer app decides whether to warn, restrict, or wipe.
- **Deep-link + transport safety**: `DeepLinkValidator` / `SecureNavHandler` (scheme/host allowlist for
  incoming deep links), `CertificatePinConfig` (consumed by `core-base/network`'s `KtorHttpClient` for
  TLS pinning).
- **In-memory hygiene**: `SensitiveString` — a `CharArray`-backed, explicitly zeroable credential
  wrapper (`AutoCloseable`), avoiding the immutable-JVM-`String` lingering-in-memory problem.
- **DI**: `SecurityModule` — zero-config, auto-included by `cmp-navigation`'s `KoinModules.allModules`;
  includes a per-platform `platformSecurityModule` expect.

## How it fits

Everything defaults to a safe no-op so the template compiles and runs unconfigured:
`CertificatePinConfig.default()` pins nothing, `SecurityPolicy.default()` uses generic thresholds. A
fork tightens pins/thresholds and consumes `FieldEncryptor` / `CertificatePinConfig` at their two real
shipped call sites — `core/database` (field encryption) and `core-base/network` (cert pinning). See
`CONSUMPTION.md`.
