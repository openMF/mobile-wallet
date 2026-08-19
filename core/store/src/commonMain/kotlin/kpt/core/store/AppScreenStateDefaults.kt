/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kpt.core.base.ui.screen.DefaultLottieAnimations
import kpt.core.base.ui.screen.ScreenStateDefaults
import kpt.core.base.ui.screen.ScreenStateEmpty
import kpt.core.base.ui.screen.ScreenStateError
import kpt.core.base.ui.screen.ScreenStateLoading
import kpt.core.base.ui.screen.ScreenStateNoNetwork
import kpt.core.base.ui.screen.ScreenStateVisual
import kpt.core.store.generated.resources.Res
import kpt.core.store.generated.resources.screenstate_captive_action
import kpt.core.store.generated.resources.screenstate_captive_message
import kpt.core.store.generated.resources.screenstate_empty_message
import kpt.core.store.generated.resources.screenstate_empty_title
import kpt.core.store.generated.resources.screenstate_error_retry
import kpt.core.store.generated.resources.screenstate_error_title
import kpt.core.store.generated.resources.screenstate_nonet_message
import kpt.core.store.generated.resources.screenstate_nonet_retry
import org.jetbrains.compose.resources.stringResource

/**
 * !! THIS IS THE FORK CUSTOMIZATION POINT !!
 *
 * Edit this file to brand your app's empty / error / no-network / loading visuals.
 * Do NOT edit `core-base/store` or `core-base/ui` — they're framework-shared.
 *
 * App-wide [ScreenStateDefaults] for `kpt.core.base.ui.ScreenContent` and
 * `kpt.core.base.ui.PagingScreenContent`. Already wired into [KptTheme] —
 * every screen wrapped by `KptTheme` automatically picks up these defaults via
 * `LocalScreenStateDefaults`. No per-screen wiring required.
 *
 * **What this composable does now (Phase 02):**
 *  - reads all user-visible copy from `stringResource(...)` so the app respects locale
 *  - wires the bundled `DefaultLottieAnimations` set (`empty.json`, `error.json`,
 *    `no_network.json`) into the empty/error/no-network visuals
 *  - routes the error-message mapping through [rememberAppErrorMessageFor] so error copy
 *    is also localized (per-category via [kpt.core.base.store.error.categorize])
 *  - wires no explicit `captivePortalAction`; [DefaultNoNetworkContent] supplies the
 *    default via [rememberOpenCaptivePortalSignIn] (backed by cmp-intent-launcher)
 *
 * Forks customize by overriding any of these calls — swap the Lottie spec to a branded
 * .json, set [ScreenStateError.onShown] to a telemetry hook, or wrap the result via
 * [kpt.core.base.ui.screen.WithScreenStateDefaults] for per-screen tweaks.
 */
@Composable
fun appScreenStateDefaults(): ScreenStateDefaults {
    val emptyTitle = stringResource(Res.string.screenstate_empty_title)
    val emptyMessage = stringResource(Res.string.screenstate_empty_message)
    val errorTitle = stringResource(Res.string.screenstate_error_title)
    val errorRetry = stringResource(Res.string.screenstate_error_retry)
    val nonetMessage = stringResource(Res.string.screenstate_nonet_message)
    val nonetRetry = stringResource(Res.string.screenstate_nonet_retry)
    val captiveMessage = stringResource(Res.string.screenstate_captive_message)
    val captiveAction = stringResource(Res.string.screenstate_captive_action)

    val errorMessageFor = rememberAppErrorMessageFor()

    return remember(
        emptyTitle,
        emptyMessage,
        errorTitle,
        errorRetry,
        nonetMessage,
        nonetRetry,
        captiveMessage,
        captiveAction,
        errorMessageFor,
    ) {
        ScreenStateDefaults(
            loading = ScreenStateLoading.Skeleton(rowCount = 5),
            empty = ScreenStateEmpty(
                visual = ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.empty),
                title = emptyTitle,
                message = emptyMessage,
            ),
            error = ScreenStateError(
                visual = ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.error),
                title = errorTitle,
                messageFor = errorMessageFor,
                retryText = errorRetry,
                // TODO(fork): wire telemetry, e.g.
                //   onShown = { error -> AppTelemetry.recordError("screen_state_error", error) },
            ),
            noNetwork = ScreenStateNoNetwork(
                message = nonetMessage,
                captivePortalMessage = captiveMessage,
                captivePortalActionText = captiveAction,
                retryText = nonetRetry,
            ),
        )
    }
}
