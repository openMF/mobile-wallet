package org.mifos.mobilewallet.mifospay.merchants

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

interface MerchantsContract {
    interface MerchantsPresenter : BasePresenter {
        fun fetchMerchants()
    }

    interface MerchantsView : BaseView<MerchantsPresenter?> {
        fun listMerchantsData(savingsWithAssociationsList: List<SavingsWithAssociations>?)
        fun showErrorStateView(drawable: Int, title: Int, subtitle: Int)
        fun showEmptyStateView()
        fun showMerchants()
        fun showMerchantFetchProcess()
    }
}