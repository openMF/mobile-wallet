/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

/**
 * Append-only test-tag registry for the settings feature.
 * Consumed by Compose UI tests in `feature/settings/src/commonTest/`.
 * APPEND-ONLY contract (RULE-KMP-COMPOSE-UITEST-001 CU-5).
 */
object TestTags {

    /** Tags for [SettingsScreen]. */
    object Settings {
        /** Root scaffold — always rendered regardless of dialog state. */
        const val SCREEN: String = "settings_screen"
    }

    /** Tags for [NotificationScreen]. */
    object Notification {
        /** Root scaffold — always rendered regardless of content state. */
        const val SCREEN: String = "notification_screen"
    }

    /** Tags for [SyncAndDraftsScreen]. */
    object SyncAndDrafts {
        /** Root scaffold — always rendered regardless of content state. */
        const val SCREEN: String = "sync_and_drafts_screen"

        /** The manual "Prune expired" button (rendered only when there is content). */
        const val PRUNE: String = "sync_and_drafts_prune"
    }
}
