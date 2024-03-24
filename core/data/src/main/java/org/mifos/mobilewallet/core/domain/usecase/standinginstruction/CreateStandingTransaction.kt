package org.mifos.mobilewallet.core.domain.usecase.standinginstruction

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import com.mifos.mobilewallet.model.entity.client.Client
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import com.mifos.mobilewallet.model.entity.standinginstruction.SDIResponse
import com.mifos.mobilewallet.model.entity.payload.StandingInstructionPayload
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


class CreateStandingTransaction @Inject constructor(private val apiRepository: FineractRepository) :
        UseCase<CreateStandingTransaction.RequestValues, CreateStandingTransaction.ResponseValue>() {

    lateinit var fromClient: Client
    lateinit var toClient: Client
    lateinit var fromAccount: SavingAccount
    lateinit var toAccount: SavingAccount

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.getSelfClientDetails(requestValues.fromClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Client) {
                        fromClient = client
                        fetchToClientData()
                    }
                })
    }

    private fun fetchToClientData() {
        apiRepository.getClientDetails(walletRequestValues.toClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client>() {

                    override fun onCompleted() {

                    }

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
        apiRepository.getSelfAccounts(walletRequestValues.fromClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientAccounts>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_FROM_ACCOUNT)
                    }

                    override fun onNext(clientAccounts: ClientAccounts) {
                        val accounts = clientAccounts.savingsAccounts
                        if (accounts != null && accounts.size != 0) {
                            var walletAccount: SavingAccount? = null
                            for (account in accounts) {
                                if (account.productId ==
                                        Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account
                                    break
                                }
                            }
                            walletAccount?.let{
                                fromAccount = walletAccount
                                fetchToAccountDetails()
                            }?: useCaseCallback.onError(Constants.NO_WALLET_FOUND)

                        } else {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_FROM_ACCOUNT)
                        }
                    }
                })
    }

    private fun fetchToAccountDetails() {
        apiRepository.getAccounts(walletRequestValues.toClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientAccounts>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_TO_ACCOUNT)
                    }

                    override fun onNext(clientAccounts: ClientAccounts) {
                        val accounts = clientAccounts.savingsAccounts
                        if (accounts != null && accounts.size != 0) {
                            var walletAccount: SavingAccount? = null
                            for (account in accounts) {
                                if (account.productId ==
                                        Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account
                                    break
                                }
                            }
                            walletAccount?.let{
                                toAccount = walletAccount
                                createNewStandingInstruction()
                            }?: useCaseCallback.onError(Constants.NO_WALLET_FOUND)

                        } else {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_TO_ACCOUNT)
                        }
                    }
                })
    }

    private fun createNewStandingInstruction() {

        val standingInstructionPayload = StandingInstructionPayload(
                fromClient.officeId,
                fromClient.id,
                2,
                "wallet standing transaction",
                1,
                2,
                1,
                fromAccount.id,
                toClient.officeId,
                toClient.id,
                2,
                toAccount.id,
                1,
                walletRequestValues.amount,
                walletRequestValues.validFrom,
                1,
                walletRequestValues.recurrenceInterval,
                2,
                "en",
                "dd MM yyyy",
                walletRequestValues.validTill,
                walletRequestValues.recurrenceOnDayMonth,
                "dd MM")
        apiRepository.createStandingInstruction(standingInstructionPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SDIResponse>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_MAKING_TRANSFER)
                    }

                    override fun onNext(sdiResponse: SDIResponse) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                })
    }

    class RequestValues(val validTill: String,
                        val validFrom: String,
                        val recurrenceInterval: Int,
                        val recurrenceOnDayMonth: String,
                        val fromClientId: Long,
                        val toClientId: Long,
                        val amount: Double ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

}