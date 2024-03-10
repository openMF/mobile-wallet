package org.mifos.mobilewallet.core.domain.usecase.invoice

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.Invoice
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 11/June/2018
 */
class FetchInvoices @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchInvoices.RequestValues, FetchInvoices.ResponseValue>() {

    class RequestValues(val clientId: String) : UseCase.RequestValues
    class ResponseValue(
        val invoiceList: List<Invoice?>
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchInvoices(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Invoice>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(invoices: List<Invoice>) {
                    useCaseCallback.onSuccess(ResponseValue(invoices))
                }
            })

    }
}
