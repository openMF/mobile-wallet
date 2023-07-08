package org.mifos.mobilewallet.mifospay.faq.presenter

import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.faq.FAQContract
import org.mifos.mobilewallet.mifospay.faq.FAQContract.FAQView
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