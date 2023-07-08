package org.mifos.mobilewallet.mifospay.faq

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * This class is a Contract Class working as Interface for UI and
 * Presenter components of FAQ setion.
 *
 * @author ankur
 * @since 11/July/2018
 */
interface FAQContract {
    /**
     * Defines all the functions in Presenter Component.
     */
    interface FAQPresenter : BasePresenter

    /**
     * Defines all the functions in UI Component.
     */
    interface FAQView : BaseView<FAQPresenter?> {
        fun initViews()
        fun initObjects()
        fun initListData()
    }
}