package org.mifos.mobilewallet.core.domain.usecase.account

import com.mifos.mobilewallet.model.entity.Page
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchMerchants @Inject constructor(
    private val mFineractRepository: FineractRepository
) : UseCase<FetchMerchants.RequestValues, FetchMerchants.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.savingsAccounts
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<SavingsWithAssociations>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.message)
                }

                override fun onNext(savingsWithAssociationsPage: Page<SavingsWithAssociations>) {
                    val savingsWithAssociationsList = savingsWithAssociationsPage.pageItems
                    val merchantsList: MutableList<SavingsWithAssociations> = ArrayList()
                    for (i in savingsWithAssociationsList.indices) {
                        if (savingsWithAssociationsList[i].savingsProductId ==
                            Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                        ) {
                            merchantsList.add(savingsWithAssociationsList[i])
                        }
                    }
                    useCaseCallback.onSuccess(ResponseValue(merchantsList))
                }
            })
    }

    class RequestValues : UseCase.RequestValues
    data class ResponseValue(
        val savingsWithAssociationsList: List<SavingsWithAssociations>
    ) : UseCase.ResponseValue
}
