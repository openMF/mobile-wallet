/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.account

import com.mifospay.core.model.entity.TPTResponse
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import com.mifospay.core.model.entity.beneficary.Beneficiary
import com.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import com.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.entity.client.ClientAccounts
import com.mifospay.core.model.entity.payload.TransferPayload
import com.mifospay.core.model.utils.DateHelper
import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@Suppress("UnusedPrivateMember")
class TransferFunds @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<TransferFunds.RequestValues, TransferFunds.ResponseValue>() {

    // override var requestValues: RequestValues
    private lateinit var fromClient: Client
    private lateinit var toClient: Client
    private lateinit var fromAccount: SavingAccount
    private lateinit var toAccount: SavingAccount

    override fun executeUseCase(requestValues: RequestValues) {
        this.walletRequestValues = requestValues
        apiRepository.getSelfClientDetails(requestValues.fromClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<Client>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Client) {
                        fromClient = client
                        fetchToClientDetails()
                    }
                },
            )
    }

    private fun fetchToClientDetails() {
        apiRepository.getClientDetails(walletRequestValues.toClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<Client>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Client) {
                        toClient = client
                        fetchFromAccountDetails()
                    }
                },
            )
    }

    private fun fetchFromAccountDetails() {
        apiRepository.getSelfAccounts(walletRequestValues.fromClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ClientAccounts>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_FROM_ACCOUNT)
                    }

                    override fun onNext(clientAccounts: ClientAccounts) {
                        val accounts = clientAccounts.savingsAccounts
                        if (accounts.isNotEmpty()) {
                            var walletAccount: SavingAccount? = null
                            for (account in accounts) {
                                if (account.productId == Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account
                                    break
                                }
                            }
                            if (walletAccount != null) {
                                fromAccount = walletAccount
                                fetchToAccountDetails()
                            } else {
                                useCaseCallback.onError(Constants.NO_WALLET_FOUND)
                            }
                        } else {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_FROM_ACCOUNT)
                        }
                    }
                },
            )
    }

    private fun fetchToAccountDetails() {
        apiRepository.getAccounts(walletRequestValues.toClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ClientAccounts>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_TO_ACCOUNT)
                    }

                    override fun onNext(clientAccounts: ClientAccounts) {
                        val accounts = clientAccounts.savingsAccounts
                        if (accounts.isNotEmpty()) {
                            var walletAccount: SavingAccount? = null
                            for (account in accounts) {
                                if (account.productId == Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account
                                    break
                                }
                            }
                            if (walletAccount != null) {
                                toAccount = walletAccount
                                makeTransfer()
                            } else {
                                useCaseCallback.onError(Constants.NO_WALLET_FOUND)
                            }
                        } else {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_TO_ACCOUNT)
                        }
                    }
                },
            )
    }

    private fun checkBeneficiary() {
        apiRepository.beneficiaryList
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<Beneficiary>>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_BENEFICIARIES)
                    }

                    override fun onNext(beneficiaries: List<Beneficiary>) {
                        var exists = false
                        beneficiaries.forEach { beneficiary ->
                            if (beneficiary.accountNumber == toAccount.accountNo) {
                                exists = true
                                if (beneficiary.transferLimit >= walletRequestValues.amount) {
                                    makeTransfer()
                                } else {
                                    updateTransferLimit(beneficiary.id?.toLong()!!)
                                }
                                return@forEach
                            }
                        }
                        if (!exists) {
                            addBeneficiary()
                        }
                    }
                },
            )
    }

    private fun addBeneficiary() {
        val payload = BeneficiaryPayload().apply {
            accountNumber = toAccount.accountNo
            name = toClient.displayName
            officeName = toClient.officeName
            transferLimit = walletRequestValues.amount.toInt()
            accountType = 2
        }

        apiRepository.createBeneficiary(payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_ADDING_BENEFICIARY)
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        makeTransfer()
                    }
                },
            )
    }

    private fun updateTransferLimit(beneficiaryId: Long) {
        val updatePayload = BeneficiaryUpdatePayload().apply {
            transferLimit = walletRequestValues.amount.toInt()
        }
        apiRepository.updateBeneficiary(beneficiaryId, updatePayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_ADDING_BENEFICIARY)
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        makeTransfer()
                    }
                },
            )
    }

    private fun makeTransfer() {
        val transferPayload = TransferPayload().apply {
            fromAccountId = fromAccount.id.toInt()
            fromClientId = fromClient.id.toLong()
            fromAccountType = 2
            fromOfficeId = fromClient.officeId
            toOfficeId = toClient.officeId
            toAccountId = toAccount.id.toInt()
            toClientId = toClient.id.toLong()
            toAccountType = 2
            transferDate = DateHelper.getDateAsStringFromLong(System.currentTimeMillis())
            transferAmount = walletRequestValues.amount
            transferDescription = Constants.WALLET_TRANSFER
        }

        apiRepository.makeThirdPartyTransfer(transferPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<TPTResponse>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_MAKING_TRANSFER)
                    }

                    override fun onNext(responseBody: TPTResponse) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                },
            )
    }

    data class RequestValues(
        val fromClientId: Long,
        val toClientId: Long,
        val amount: Double,
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
