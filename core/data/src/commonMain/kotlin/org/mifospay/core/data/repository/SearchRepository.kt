/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.DataState
import org.mifospay.core.model.search.SearchResult

interface SearchRepository {
    suspend fun searchResources(
        query: String,
        resources: String,
        exactMatch: Boolean,
    ): DataState<List<SearchResult>>
}
