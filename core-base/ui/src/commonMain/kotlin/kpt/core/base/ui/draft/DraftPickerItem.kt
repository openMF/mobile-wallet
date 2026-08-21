/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.draft

/**
 * One selectable draft in the [DraftPickerList] — a **UI value object**, deliberately independent of
 * the store/database types so this framework primitive never leaks `core-base/store` into its public
 * API (consumers map their `DraftRecord` / outbox entry to this).
 *
 * @param id       Stable handle passed back to the picker's callbacks.
 * @param title    Primary line (e.g. the entity being edited — "Rent · ₹1,200").
 * @param subtitle Optional secondary line (e.g. "Saved 2h ago" or the failure reason).
 * @param isFailed When true the row is styled as a failed sync (error accent).
 */
data class DraftPickerItem(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val isFailed: Boolean = false,
)
