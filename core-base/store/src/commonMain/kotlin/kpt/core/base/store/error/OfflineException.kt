/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.error

/**
 * Synthetic signal that the device was offline at the moment a Store load was
 * attempted. Used by [PagingScreenStream] (and any other framework loader) to fail
 * fast without burning network retry budget when there is clearly no point trying.
 *
 * The class name contains "Offline" so [categorize] returns [ErrorCategory.Network]
 * and downstream UI (LoadMoreFooter, DefaultErrorContent via `messageFor`) routes
 * through the no-network treatment automatically.
 *
 * Multiplatform-safe: extends [RuntimeException] (no kotlinx-io dependency).
 */
class OfflineException(message: String = "Device is offline") : RuntimeException(message)
