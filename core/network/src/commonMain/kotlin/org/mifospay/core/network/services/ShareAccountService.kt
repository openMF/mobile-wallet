/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.network.services

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.network.model.entity.shareAccount.ShareWithAssociationsResponseDto
import org.mifospay.core.network.utils.ApiEndPoints

interface ShareAccountService {

    @GET(ApiEndPoints.SHARE_ACCOUNTS + "/{accountId}")
    fun getShareAccountDetails(
        @Path("accountId") accountId: Long,
        @Query("associations") associations: String = "all",
    ): Flow<ShareWithAssociationsResponseDto>
}
