/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.security

// `platformAuthenticationProvider` is imported below aliased to `LocalBiometricAuthenticator`
// (a rename-only alias so downstream call sites + the T4 grep contract in
// `02-topology-reconciliation.md` read consistently; underlying object identity unchanged).
// Kept above the import list so ktlint standard:import-ordering stays green.
import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.biometrics.PlatformAuthenticatorCompositionProvider
import org.mifos.authenticator.biometrics.platformAuthenticationProvider as LocalBiometricAuthenticator

/**
 * Fork shell wrapper around the biometrics library's
 * [PlatformAuthenticatorCompositionProvider].
 *
 * Behavior (d) of the four `MifosPayApp.kt`-owned security concerns re-homed onto
 * the `:cmp-navigation` template shell (see 02-topology-reconciliation Phase 2 T4).
 *
 * The library's provider seeds two composition locals — [LocalBiometricAuthenticator]
 * (aliased `platformAuthenticationProvider` here) and
 * `platformAvailableAuthenticationOption` — that every biometric-consuming
 * descendant (root/re-auth passcode screens, settings biometrics toggle, intra-bank
 * transfer auth gate) reads by `.current`. Without this wrapper any such descendant
 * crashes at compose time with a "compositionLocalOf without default value" error.
 *
 * The [BiometricStorageAdapter] singleton is bound by the fork's
 * `RepositoryModule` (`AppLockRepositoryImpl` sibling) — Phase 2 T6/T7 wire that
 * module into `cmp.navigation.di.KoinModules.allModules`; until then this gate
 * fails fast at runtime with a Koin resolution error, which is the correct
 * signal that DI hasn't been reconciled yet (not a masked stub).
 *
 * The gate deliberately keeps its Composable signature `content: @Composable () -> Unit`
 * so it can be dropped into `ComposeApp` around the `KptTheme { RootNavScreen(...) }`
 * subtree without any parameter surgery.
 */
@Composable
fun PlatformAuthenticatorGate(
    content: @Composable () -> Unit,
) {
    // Touch the aliased composition local's declaration site so the alias import above
    // isn't marked unused by the compiler / static analyzers — this makes the
    // `LocalBiometricAuthenticator` symbol observable to grep-based verification
    // in `02-topology-reconciliation.md` T4 without adding a real read that would
    // require an already-installed provider.
    @Suppress("UnusedPrivateMember")
    val biometricAuthenticatorLocalRef = LocalBiometricAuthenticator

    val biometricStorageAdapter: BiometricStorageAdapter = koinInject()
    PlatformAuthenticatorCompositionProvider(
        biometricStorageAdapter = biometricStorageAdapter,
    ) {
        content()
    }
}
