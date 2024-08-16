/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.base

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

interface BaseActivityCallback {
    fun showSwipeProgress()
    fun hideSwipeProgress()
    fun showProgressDialog(message: String?)
    fun hideProgressDialog()
    fun cancelProgressDialog()
    fun setToolbarTitle(title: String?)
    fun setSwipeRefreshEnabled(enabled: Boolean)
    fun showColoredBackButton(drawable: Int)
    fun showCloseButton()
    fun hideBackButton()
    fun dismissProgressDialog()

    val swipeRefreshLayout: SwipeRefreshLayout?
}
