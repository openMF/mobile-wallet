package org.mifos.mobilewallet.mifospay.base

interface BasePresenter {
    fun attachView(baseView: BaseView<*>?)
}