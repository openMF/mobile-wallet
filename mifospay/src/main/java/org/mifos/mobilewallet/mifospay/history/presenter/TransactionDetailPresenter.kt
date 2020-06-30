package org.mifos.mobilewallet.mifospay.history.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.customer.FetchCustomerDetails
import org.mifos.mobilewallet.core.domain.usecase.deposit.FetchDepositAccount
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.history.HistoryContract
import javax.inject.Inject

class TransactionDetailPresenter @Inject constructor (
        val mUseCaseHandler: UseCaseHandler, val preferencesHelper: PreferencesHelper)
    : HistoryContract.TransactionDetailPresenter {

    lateinit var mTransactionDetailView: HistoryContract.TransactionDetailView

    @Inject
    lateinit var fetchDepositAccountUseCase: FetchDepositAccount

    @Inject
    lateinit var fetchCustomerUseCase: FetchCustomerDetails

    override fun attachView(baseView: BaseView<*>?) {
        mTransactionDetailView = baseView as HistoryContract.TransactionDetailView
        mTransactionDetailView.setPresenter(this)
    }

    override fun fetchAccountDetail(accountIdentifier: String) {
        mTransactionDetailView.showProgressBar()
        mUseCaseHandler.execute(fetchDepositAccountUseCase,
                FetchDepositAccount.RequestValues(accountIdentifier), object :
                UseCaseCallback<FetchDepositAccount.ResponseValue> {

            override fun onSuccess(response: FetchDepositAccount.ResponseValue) {
                val customerIdentifier = response.depositAccount.customerIdentifier
                customerIdentifier?.let {
                    fetchCustomerDetails(customerIdentifier)
                } ?: showError("An Error Occurred")
            }

            override fun onError(message: String) =
                    showError(message)
        })
    }

    private fun fetchCustomerDetails(customerIdentifier: String) {
        mUseCaseHandler.execute(fetchCustomerUseCase,
                FetchCustomerDetails.RequestValues(customerIdentifier),
                object : UseCaseCallback<FetchCustomerDetails.ResponseValue> {
                    override fun onSuccess(response: FetchCustomerDetails.ResponseValue) {
                        mTransactionDetailView.hideProgressBar()
                        val customer = response.customer
                        val customerName = "${customer.firstName} ${customer.lastName}"
                        mTransactionDetailView.showCustomerName(customerName)
                    }

                    override fun onError(message: String) =
                            showError(message)
                })
    }

    private fun showError(message: String) {
        mTransactionDetailView.hideProgressBar()
        mTransactionDetailView.showError(message)
    }

}