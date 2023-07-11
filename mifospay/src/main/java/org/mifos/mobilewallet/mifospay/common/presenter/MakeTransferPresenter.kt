package org.mifos.mobilewallet.mifospay.common.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.TransferFunds
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.common.TransferContract
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
class MakeTransferPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    TransferContract.TransferPresenter {
    @JvmField
    @Inject
    var transferFunds: TransferFunds? = null

    @JvmField
    @Inject
    var searchClient: SearchClient? = null
    private var mTransferView: TransferContract.TransferView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mTransferView = baseView as TransferContract.TransferView?
        mTransferView?.setPresenter(this)
    }

    override fun fetchClient(externalId: String?) {
        mUsecaseHandler.execute(searchClient, SearchClient.RequestValues(externalId),
            object : UseCaseCallback<SearchClient.ResponseValue?> {
                override fun onSuccess(response: SearchClient.ResponseValue?) {
                    val searchResult = response?.results?.get(0)
                    searchResult?.resultId?.let {
                        mTransferView?.showToClientDetails(
                            it.toLong(),
                            searchResult.resultName, externalId
                        )
                    }
                }

                override fun onError(message: String) {
                    mTransferView?.showVpaNotFoundSnackbar()
                }
            })
    }

    override fun makeTransfer(fromClientId: Long, toClientId: Long, amount: Double) {
        mTransferView?.enableDragging(false)
        mUsecaseHandler.execute(transferFunds,
            TransferFunds.RequestValues(fromClientId, toClientId, amount),
            object : UseCaseCallback<TransferFunds.ResponseValue?> {
                override fun onSuccess(response: TransferFunds.ResponseValue?) {
                    mTransferView?.enableDragging(true)
                    mTransferView?.transferSuccess()
                }

                override fun onError(message: String) {
                    mTransferView?.enableDragging(true)
                    mTransferView!!.transferFailure()
                }
            })
    }
}