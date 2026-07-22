/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.ui.providerWebView

/**
 * Maps a loan provider id (see `org.mifos.lib.loan.ui.selectLoanProvider.LoanProviderOption`) to
 * the website rendered on [LoanProviderWebViewScreen].
 *
 * There's no per-provider backend yet, so every provider currently points at the same test URL;
 * this is the single place to swap in the real per-provider URLs once they exist.
 */
internal fun urlForLoanProvider(providerId: String): String = "https://www.google.com"
