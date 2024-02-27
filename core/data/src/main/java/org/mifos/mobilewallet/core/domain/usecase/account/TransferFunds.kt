package org.mifos.mobilewallet.core.domain.usecase.account

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import com.mifos.mobilewallet.model.entity.beneficary.Beneficiary
import com.mifos.mobilewallet.model.entity.beneficary.BeneficiaryPayload
import com.mifos.mobilewallet.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifos.mobilewallet.model.entity.payload.TransferPayload
import com.mifos.mobilewallet.model.entity.TPTResponse
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import com.mifos.mobilewallet.model.entity.client.Client
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import com.mifos.mobilewallet.model.utils.DateHelper
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class TransferFunds @Inject constructor( private val apiRepository: FineractRepository) :
    UseCase<TransferFunds.RequestValues, TransferFunds.ResponseValue>() {

    private lateinit var requestValues: RequestValues
    private lateinit var fromClient: Client
    private lateinit var toClient: Client
    private lateinit var fromAccount: SavingAccount
    private lateinit var toAccount: SavingAccount

    override fun executeUseCase(requestValues: RequestValues) {
        this.requestValues = requestValues
        apiRepository.getSelfClientDetails(requestValues.fromClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                }

                override fun onNext(client: Client) {
                    fromClient = client
                    fetchToClientDetails()
                }
            })
    }

    private fun fetchToClientDetails() {
        apiRepository.getClientDetails(requestValues.toClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                }

                override fun onNext(client: Client) {
                    toClient = client
                    fetchFromAccountDetails()
                }
            })
    }

    private fun fetchFromAccountDetails() {
        apiRepository.getSelfAccounts(requestValues.fromClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_FROM_ACCOUNT)
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    val accounts = clientAccounts.savingsAccounts
                    if (accounts != null && accounts.isNotEmpty()) {
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
            })
    }

    private fun fetchToAccountDetails() {
        apiRepository.getAccounts(requestValues.toClientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_TO_ACCOUNT)
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    val accounts = clientAccounts.savingsAccounts
                    if (!accounts.isNullOrEmpty()) {
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
            })
    }

    private fun checkBeneficiary() {
        apiRepository.beneficiaryList
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Beneficiary>>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_BENEFICIARIES)
                }

                override fun onNext(beneficiaries: List<Beneficiary>) {
                    var exists = false
                    beneficiaries.forEach { beneficiary ->
                        if (beneficiary.accountNumber == toAccount.accountNo) {
                            exists = true
                            if (beneficiary.transferLimit >= requestValues.amount) {
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
            })
    }

    private fun addBeneficiary() {
        val payload = BeneficiaryPayload().apply {
            accountNumber = toAccount.accountNo
            name = toClient.displayName
            officeName = toClient.officeName
            transferLimit = requestValues.amount.toInt()
            accountType = 2
        }

        apiRepository.createBeneficiary(payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_ADDING_BENEFICIARY)
                }

                override fun onNext(responseBody: ResponseBody) {
                    makeTransfer()
                }
            })
    }

    private fun updateTransferLimit(beneficiaryId: Long) {
        val updatePayload = BeneficiaryUpdatePayload().apply {
            transferLimit = requestValues.amount.toInt()
        }
        apiRepository.updateBeneficiary(beneficiaryId, updatePayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_ADDING_BENEFICIARY)
                }

                override fun onNext(responseBody: ResponseBody) {
                    makeTransfer()
                }
            })
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
            transferAmount = requestValues.amount
            transferDescription = Constants.WALLET_TRANSFER
        }

        apiRepository.makeThirdPartyTransfer(transferPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<TPTResponse>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_MAKING_TRANSFER)
                }

                override fun onNext(responseBody: TPTResponse) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })
    }

    data class RequestValues(
        val fromClientId: Long,
        val toClientId: Long,
        val amount: Double
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
