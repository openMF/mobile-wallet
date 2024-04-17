package org.mifospay.base

interface BasePresenter {
    fun attachView(baseView: BaseView<*>?)
}