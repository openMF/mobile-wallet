package org.mifos.mobilewallet.mifospay.base

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