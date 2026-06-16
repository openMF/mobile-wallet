/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.navigation

/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */

import androidx.navigation.NavHostController

/**
 * Reads the stored URI from [PendingDeepLinkStore] and navigates to it.
 *
 * Lives in commonMain — [NavHostController] is from androidx.navigation
 * which is multiplatform, so this is legal here.
 *
 * Platform-specific detail (reconstructing an Intent on Android, handling
 * a URL on iOS) is hidden inside each actual implementation.
 *
 * Called once from the MAIN_GRAPH composable in [RootNavGraph], which
 * only composes after the passcode gate has been cleared.
 */
expect fun consumePendingDeepLink(navController: NavHostController)
