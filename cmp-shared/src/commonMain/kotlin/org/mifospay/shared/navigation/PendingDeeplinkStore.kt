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

/**
 * Platform-agnostic store for a pending deep-link URI string.
 *
 * Lives in commonMain — zero platform imports.
 * Stores only a URI string, not an android.content.Intent.
 *
 * Android actual: backed by a simple in-memory variable.
 * iOS actual:     same — iOS deep links use a URL string too.
 */
expect object PendingDeepLinkStore {

    /** Store a URI string to be consumed after the auth gate clears. */
    fun store(uri: String)

    /** Returns the stored URI and clears it, or null if none pending. */
    fun consume(): String?

    /** Clear without consuming — call on logout. */
    fun clear()
}