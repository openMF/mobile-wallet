/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.compose.runtime.Composable
import kpt.core.base.store.infra.DraftRecord
import kpt.core.base.store.submit.SubmitOutboxStatus
import kpt.core.base.ui.draft.DraftPickerItem
import kpt.core.base.ui.draft.DraftPickerList
import kpt.core.designsystem.theme.KptTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/*
 * Device-free CMP render-tier previews (SCREENSHOT_TEST.md CMP-PRIMARY) for the E4 draft surfaces.
 * `CommonComposablePreviewScanner` auto-discovers each `internal @Preview` in this package and
 * renders it off desktopTest via `verifyRoborazziDesktop` — no emulator, no Robolectric.
 * (Previews MUST be `internal`, never `private` — the scanner's reflective invoke can't see private,
 * which silently renders nothing = vacuous green.)
 */

/** Sync & Drafts — populated: a failed, a retrying, and two pending drafts across forms. */
@Preview
@Composable
internal fun SyncAndDraftsPopulatedPreview() {
    KptTheme {
        SyncAndDraftsScreenContent(
            uiState = SyncAndDraftsUiState.Success(
                drafts = listOf(
                    DraftRecord(3, "bill", "electricity", SubmitOutboxStatus.PENDING, 0, 0, null),
                    DraftRecord(4, "loan", "home-loan", SubmitOutboxStatus.PENDING, 0, 0, null),
                ),
                syncing = listOf(
                    DraftRecord(2, "alert", "btc-alert", SubmitOutboxStatus.RETRYING, 0, 0, null),
                ),
                failed = listOf(
                    DraftRecord(1, "bill", "rent", SubmitOutboxStatus.FAILED, 0, 0, "Network timeout — will retry"),
                ),
            ),
            onBackClick = {},
            onRetry = {},
            onDiscard = {},
            onPrune = {},
        )
    }
}

/** Sync & Drafts — empty: nothing pending or failed. */
@Preview
@Composable
internal fun SyncAndDraftsEmptyPreview() {
    KptTheme {
        SyncAndDraftsScreenContent(
            uiState = SyncAndDraftsUiState.Success(drafts = emptyList(), syncing = emptyList(), failed = emptyList()),
            onBackClick = {},
            onRetry = {},
            onDiscard = {},
            onPrune = {},
        )
    }
}

/** Full-screen draft picker (multi-pending): the user chooses which draft to resume. */
@Preview
@Composable
internal fun DraftPickerListPreview() {
    KptTheme {
        DraftPickerList(
            items = listOf(
                DraftPickerItem(1, "Rent · ₹1,200", "Saved offline — failed to sync", isFailed = true),
                DraftPickerItem(2, "Electricity", "Pending sync"),
                DraftPickerItem(3, "Internet · ₹800", "Pending sync"),
            ),
            onResume = {},
            onDiscard = {},
            onStartFresh = {},
            resumeLabel = "Resume",
            discardLabel = "Discard",
            startFreshLabel = "Start a new entry",
        )
    }
}
