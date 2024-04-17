package org.mifospay.base

interface BaseView<T : BasePresenter?> {
    fun setPresenter(presenter: T)
}