package org.mifos.mobilewallet.mifospay.paymenthub.presenter

import android.content.Context
import org.json.JSONObject
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.data.paymenthub.api.PaymentHubApiManager
import org.mifos.mobilewallet.core.data.paymenthub.entity.Amount
import org.mifos.mobilewallet.core.data.paymenthub.entity.PartyIdInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactingEntity
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionType
import org.mifos.mobilewallet.core.data.paymenthub.entity.User
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.CreateTransaction
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.DecodeQR
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.EncodeQR
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.FetchTransactionInfo
import org.mifos.mobilewallet.core.utils.IOUtils
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.paymenthub.TransactionContract

import javax.inject.Inject

class TransactionPresenter @Inject constructor(val useCaseHandler: UseCaseHandler):
        TransactionContract.TransactionPresenter {

    private lateinit var transactionView: TransactionContract.TransactionView

    @set:Inject
    lateinit var encodeQR: EncodeQR

    @set:Inject
    lateinit var decodeQR: DecodeQR

    @set:Inject
    lateinit var createTransaction: CreateTransaction

    @set:Inject
    lateinit var fetchTransactionInfo: FetchTransactionInfo

    override fun attachView(baseView: BaseView<*>?) {
        transactionView = baseView as TransactionContract.TransactionView
    }

    override fun createQRData(qrData: QRData) {
        useCaseHandler.execute(encodeQR, EncodeQR.RequestValues(qrData),
                object : UseCase.UseCaseCallback<EncodeQR.ResponseValue> {
                    override fun onSuccess(response: EncodeQR.ResponseValue?) {
                        response?.let {
                            transactionView.showQR(it.qrString)
                        } ?: run {
                            onError("Unable to encode qr")
                        }
                    }
                    override fun onError(message: String?) {

                    }
                } )
    }

    override fun decodeQRData(qrString: String) {
        useCaseHandler.execute(decodeQR, DecodeQR.RequestValues(qrString),
                object : UseCase.UseCaseCallback<DecodeQR.ResponseValue> {
                    override fun onSuccess(response: DecodeQR.ResponseValue?) {
                        response?.let {
                            transactionView.qrDecoded(it.qrData)
                        } ?: run {
                            onError("Unable to decode qr")
                        }
                    }
                    override fun onError(message: String?) {

                    }
                } )
    }

    override fun createTransaction(transaction: Transaction) {
        useCaseHandler.execute(createTransaction, CreateTransaction.RequestValues(transaction),
                object : UseCase.UseCaseCallback<CreateTransaction.ResponseValue> {
                    override fun onSuccess(response: CreateTransaction.ResponseValue?) {
                        response?.let {
                            transactionView.transactionCreated(it.transactionInfo)
                        } ?: kotlin.run {
                            onError("Unable to create transaction")
                        }
                    }
                    override fun onError(message: String?) {
                        transactionView.showTransactionError("Failed to initiate payment")
                    }
                } )
    }

    override fun fetchTransactionInfo(transactionId: String) {
        useCaseHandler.execute(fetchTransactionInfo, FetchTransactionInfo.RequestValues(transactionId),
                object : UseCase.UseCaseCallback<FetchTransactionInfo.ResponseValue> {
                    override fun onSuccess(response: FetchTransactionInfo.ResponseValue?) {
                        response?.let {
                            transactionView.showTransactionStatus(it.transactionStatus)
                        } ?: kotlin.run {
                            onError("Unable to fetch transaction status")
                        }
                    }
                    override fun onError(message: String?) {
                        android.os.Handler().postDelayed({
                            fetchTransactionInfo(transactionId)
                        }, 3000)
                    }
                } )
    }

    fun qrDataToTransaction(qrData: QRData, currentUser: User): Transaction {
        return Transaction(
                qrData.clientRefId!!,
                TransactingEntity(
                        PartyIdInfo().apply {
                            partyIdType = qrData.idType
                            partyIdentifier = qrData.id
                        },
                        null,
                        qrData.name!!
                ),
                TransactingEntity(
                        PartyIdInfo().apply {
                            partyIdType = currentUser.banks.get(0).partyIdInfo.partyIdType
                            partyIdentifier = currentUser.banks.get(0).partyIdInfo.partyIdentifier
                        },
                        null,
                        "${currentUser.firstName} ${currentUser.lastName}"
                ),
                "SEND",
                Amount().apply {
                    currency = qrData.currency
                    amount = qrData.amount
                },
                TransactionType().apply {
                    scenario = "TRANSFER"
                    initiator = "PAYER"
                    initiatorType = "CONSUMER"
                },
                qrData.note ?: ""

        )
    }

    fun manualDataToTransaction(clientRefId: String, amountString: String, note: String, party_IdType:
    String, party_Identifier: String, currentUser: User, context: Context): Transaction {
        return Transaction(
                clientRefId,
                TransactingEntity(
                        PartyIdInfo().apply {
                            partyIdType = party_IdType
                            partyIdentifier = party_Identifier
                        },
                        null,
                        getPayeeName(party_Identifier,context)
                ),
                TransactingEntity(
                        PartyIdInfo().apply {
                            partyIdType = currentUser.banks.get(0).partyIdInfo.partyIdType
                            partyIdentifier = currentUser.banks.get(0).partyIdInfo.partyIdentifier
                        },
                        null,
                        "${currentUser.firstName} ${currentUser.lastName}"
                ),
                "SEND",
                Amount().apply {
                    currency = "TZS"
                    amount = amountString
                },
                TransactionType().apply {
                    scenario = "TRANSFER"
                    initiator = "PAYER"
                    initiatorType = "CONSUMER"
                },
                note
        )
    }

    fun getPayeeName(payeeIdentifier : String, context: Context) : String{
        var payeeName = "inValid"
        val users = JSONObject(IOUtils.loadJSONFromAsset(context,
                "paymenthub_users.json")).getJSONArray("users")

        for (i in 0 until users.length()) {
            if (payeeIdentifier
                            .equals(users.getJSONObject(i).getJSONArray("banks").
                                    getJSONObject(0).getJSONObject("partyIdInfo").getString("partyIdentifier"))) {
                payeeName = users.getJSONObject(i).
                        getString("firstName") + " " + users.getJSONObject(i).getString("lastName")
                break
            }
        }
        return payeeName
    }

    override fun updateEndPoints(fspName : String, headerTenant : String) {
        PaymentHubApiManager.createService(fspName,headerTenant)
    }
}