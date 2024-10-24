/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.di

import org.koin.core.qualifier.named

val SelfServiceApi = named("SelfServiceApi")
val FineractApi = named("FineractApi")
val Testing = named("Testing")
val FineractAuthenticationService = named("FineractAuthenticationService")
val FineractClientService = named("FineractClientService")
val FineractSavingsAccountsService = named("FineractSavingsAccountsService")
val FineractRegistrationService = named("FineractRegistrationService")
val FineractThirdPartyTransferService = named("FineractThirdPartyTransferService")

val SelfServiceAuthenticationService = named("SelfServiceAuthenticationService")
val SelfServiceClientService = named("SelfServiceClientService")
val SelfServiceSavingsAccountsService = named("SelfServiceSavingsAccountsService")
val SelfServiceRegistrationService = named("SelfServiceRegistrationService")
val SelfServiceThirdPartyTransferService = named("SelfServiceThirdPartyTransferService")
