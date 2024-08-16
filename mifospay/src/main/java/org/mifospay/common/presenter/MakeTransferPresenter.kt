/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common.presenter

import org.mifospay.base.BaseView
import org.mifospay.common.TransferContract
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.TransferFunds
import org.mifospay.core.data.domain.usecase.client.SearchClient
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
class MakeTransferPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    TransferContract.TransferPresenter {

    @Inject
    lateinit var transferFunds: TransferFunds

    @Inject
    lateinit var searchClient: SearchClient

    private var mTransferView: TransferContract.TransferView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mTransferView = baseView as TransferContract.TransferView?
        mTransferView?.setPresenter(this)
    }

    override fun fetchClient(externalId: String) {
        mUsecaseHandler.execute(
            searchClient,
            SearchClient.RequestValues(externalId),
            object : UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    val searchResult = response.results[0]
                    searchResult.resultId.let {
                        mTransferView?.showToClientDetails(
                            it.toLong(),
                            searchResult.resultName,
                            externalId,
                        )
                    }
                }

                override fun onError(message: String) {
                    mTransferView?.showVpaNotFoundSnackbar()
                }
            },
        )
    }

    override fun makeTransfer(fromClientId: Long, toClientId: Long, amount: Double) {
        mTransferView?.enableDragging(false)
        mUsecaseHandler.execute(
            transferFunds,
            TransferFunds.RequestValues(fromClientId, toClientId, amount),
            object : UseCaseCallback<TransferFunds.ResponseValue> {
                override fun onSuccess(response: TransferFunds.ResponseValue) {
                    mTransferView?.enableDragging(true)
                    mTransferView?.transferSuccess()
                }

                override fun onError(message: String) {
                    mTransferView?.enableDragging(true)
                    mTransferView?.transferFailure()
                }
            },
        )
    }
}
