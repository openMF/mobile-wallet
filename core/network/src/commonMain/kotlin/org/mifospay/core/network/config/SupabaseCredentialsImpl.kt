/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.config

import org.mifos.corebase.network.SupabaseCredentials as GenericSupabaseCredentials

/**
 * Implementation of SupabaseCredentials that bridges the generated
 * SupabaseCredentials object with the generic interface.
 */
internal object SupabaseCredentialsImpl : GenericSupabaseCredentials {
    override val url: String get() = SupabaseCredentials.URL
    override val anonKey: String get() = SupabaseCredentials.ANON_KEY
    override val isConfigured: Boolean get() = SupabaseCredentials.isConfigured
}
