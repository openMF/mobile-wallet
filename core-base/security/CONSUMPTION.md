# Consuming `core-base/security`

> Framework-shared crypto/session/tamper primitives — no `core/security` wrapper. A fork binds
> `SecurityConfig` / `SecurityPolicy` and reads the two real integration points directly.

## Call sequence

1. `SecurityModule` is auto-included (`cmp-navigation`'s `KoinModules.allModules`) — it registers
   `SecurityConfig()`, `SecurityPolicy.default()`, `TamperDetector()`, `SecureWiper()`,
   `BiometricAuthenticator()`, `FailedAttemptTracker`, `SessionManager`, `DeepLinkValidator`,
   `SecureNavHandler`, and `SecureAuthManager` with defaults. You don't call this yourself.
2. Wrap the app root in `SecurityGate { AppTheme { NavHost(...) } }` — it injects `TamperDetector`,
   `SessionManager`, and `BiometricAuthenticator` from Koin and provides `LocalSecurityState`; read
   `LocalSecurityState.current.isLocked` / `.isCompromised` in composables to react (lock screen,
   tamper warning) — `SecurityGate` itself never blocks UI or navigates.
3. On a login/auth screen, inject `SecureAuthManager` and call `onAuthSuccess()` /
   `onAuthFailure()` — the latter returns `FailureAction.ATTEMPT_RECORDED | LOCKED_OUT | DATA_WIPED`
   so the UI responds without re-deriving lockout math itself.
4. **Field encryption**: inject `FieldEncryptor` and call `install(fieldEncryptor)` on your Room
   `TypeConverters` object before the database opens — the shipped pattern is `core/database`'s
   `ChargeTypeConverters.install(get<FieldEncryptor>())`. Encrypts to a `"ENC:"`-prefixed Base64 string.
5. **Certificate pinning**: pass `CertificatePinConfig(pins = mapOf("api.example.com" to listOf(...)))`
   into `setupDefaultHttpClient` (`core-base/network`) — defaults to `CertificatePinConfig.default()`
   (no pinning), so pinning is fully opt-in per fork.
6. **Deep links**: override the `DeepLinkValidator(allowedSchemes, allowedHosts)` Koin binding for your
   app's schemes/hosts, then call `secureNavHandler.sanitizeDeepLink(uri)?.let { navigate(it) }` before
   acting on an incoming URI.

## Notes

- Safe to extend/override: `SecurityConfig`, `SecurityPolicy` thresholds, `CertificatePinConfig.pins`,
  `DeepLinkValidator`'s allowed sets — all plain data classes bound via Koin; override the binding.
- Framework-owned, don't fork: the `expect`/`actual` primitives themselves (`SecureKeyProvider`,
  `FieldEncryptor`, `SecureRandom`, `TamperDetector`, `SecureWiper`, `BiometricAuthenticator`) — fix a
  wrong platform actual upstream, per the template-fix-upstream discipline.
- `SensitiveString.fromString(value)` + `.close()` (it's `AutoCloseable`) whenever a password/token
  needs explicit zeroing instead of lingering as an immutable JVM `String`.

Canonical example: `core/database`'s `ChargeTypeConverters` (`FieldEncryptor`) and `core-base/network`'s
`KtorHttpClient` (`CertificatePinConfig`) are the two shipped real call sites.

Symbols: SecurityGate, SecureAuthManager, FailedAttemptTracker, FieldEncryptor, CertificatePinConfig, DeepLinkValidator, SensitiveString, SecurityModule
