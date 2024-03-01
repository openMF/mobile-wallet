package org.mifos.mobilewallet.core.domain.usecase.account


import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import com.mifos.mobilewallet.model.entity.Page
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.core.utils.Constants
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import javax.inject.Inject

/**
 * Created by niranjan on 1/3/2024
 */
class FetchMerchants @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchMerchants.RequestValues, FetchMerchants.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.savingsAccounts
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { savingsWithAssociationsPage ->
                val savingsWithAssociationsList =
                    savingsWithAssociationsPage.pageItems
                val merchantsList =savingsWithAssociationsList.filter{
                    savingsWithAssociations ->
                savingsWithAssociations.savingsProductId == Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
            }.toMutableList()

                useCaseCallback.onSuccess(ResponseValue(merchantsList))
            }
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val savingsWithAssociationsList: List<SavingsWithAssociations>) :
        UseCase.ResponseValue
}
