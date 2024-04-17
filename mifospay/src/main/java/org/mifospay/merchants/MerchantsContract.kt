package org.mifospay.merchants

import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

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