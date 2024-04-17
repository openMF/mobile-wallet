package org.mifospay.faq.presenter

import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.faq.FAQContract
import org.mifospay.faq.FAQContract.FAQView
import javax.inject.Inject

/**
 * This class is the Presenter component of the Architecture.
 *
 * @author ankur
 * @since 11/July/2018
 */
class FAQPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : FAQContract.FAQPresenter {
    var mSettingsView: FAQView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mSettingsView = baseView as FAQView?
        mSettingsView?.setPresenter(this)
    }
}