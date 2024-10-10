/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network

class SelfServiceApiManager(
    private val ktorfitClient: KtorfitClient,
) {
    val authenticationApi by lazy { ktorfitClient.authenticationApi }

    val clientsApi by lazy { ktorfitClient.clientsApi }

    val savingAccountsListApi by lazy { ktorfitClient.savingsAccountsApi }

    val registrationAPi by lazy { ktorfitClient.registrationAPi }

    val beneficiaryApi by lazy { ktorfitClient.beneficiaryApi }

    val thirdPartyTransferApi by lazy { ktorfitClient.thirdPartyTransferApi }
}
