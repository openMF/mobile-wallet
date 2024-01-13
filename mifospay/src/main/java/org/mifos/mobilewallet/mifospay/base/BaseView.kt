package org.mifos.mobilewallet.mifospay.base

interface BaseView<T : BasePresenter?> {
    fun setPresenter(presenter: T)
}