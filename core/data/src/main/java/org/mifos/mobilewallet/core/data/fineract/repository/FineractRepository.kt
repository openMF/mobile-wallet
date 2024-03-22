package org.mifos.mobilewallet.core.data.fineract.repository

import com.mifos.mobilewallet.model.domain.NewAccount
import com.mifos.mobilewallet.model.domain.NotificationPayload
import com.mifos.mobilewallet.model.domain.twofactor.AccessToken
import com.mifos.mobilewallet.model.domain.twofactor.DeliveryMethod
import com.mifos.mobilewallet.model.domain.user.NewUser
import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.Invoice
import com.mifos.mobilewallet.model.entity.Page
import com.mifos.mobilewallet.model.entity.SearchedEntity
import com.mifos.mobilewallet.model.entity.TPTResponse
import com.mifos.mobilewallet.model.entity.UserEntity
import com.mifos.mobilewallet.model.entity.UserWithRole
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import com.mifos.mobilewallet.model.entity.accounts.savings.Transactions
import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import com.mifos.mobilewallet.model.entity.authentication.AuthenticationPayload
import com.mifos.mobilewallet.model.entity.beneficary.Beneficiary
import com.mifos.mobilewallet.model.entity.beneficary.BeneficiaryPayload
import com.mifos.mobilewallet.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifos.mobilewallet.model.entity.client.Client
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import com.mifos.mobilewallet.model.entity.payload.StandingInstructionPayload
import com.mifos.mobilewallet.model.entity.payload.TransferPayload
import com.mifos.mobilewallet.model.entity.register.RegisterPayload
import com.mifos.mobilewallet.model.entity.register.UserVerify
import com.mifos.mobilewallet.model.entity.savedcards.Card
import com.mifos.mobilewallet.model.entity.standinginstruction.SDIResponse
import com.mifos.mobilewallet.model.entity.standinginstruction.StandingInstruction
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.domain.usecase.client.CreateClient
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser
import org.mifos.mobilewallet.core.utils.Constants
import org.mifos.mobilewallet.mifospay.network.FineractApiManager
import org.mifos.mobilewallet.mifospay.network.GenericResponse
import org.mifos.mobilewallet.mifospay.network.SelfServiceApiManager
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by naman on 16/6/17.
 */
@Singleton
class FineractRepository @Inject constructor(
    private val fineractApiManager: FineractApiManager,
    private val selfApiManager: SelfServiceApiManager
) {
    fun createClient(newClient: com.mifos.mobilewallet.model.domain.client.NewClient): Observable<CreateClient.ResponseValue> {
        return fineractApiManager.clientsApi.createClient(newClient)
    }

    fun createUser(user: NewUser): Observable<CreateUser.ResponseValue> {
        return fineractApiManager.userApi.createUser(user)
    }

    fun updateUser(updateUserEntity: Any, userId: Int): Observable<GenericResponse> {
        return fineractApiManager.userApi.updateUser(userId, updateUserEntity)
    }

    fun registerUser(registerPayload: RegisterPayload): Observable<ResponseBody> {
        return fineractApiManager.registrationAPi.registerUser(registerPayload)
    }

    fun deleteUser(userId: Int): Observable<GenericResponse> {
        return fineractApiManager.userApi.deleteUser(userId)
    }

    fun verifyUser(userVerify: UserVerify): Observable<ResponseBody> {
        return fineractApiManager.registrationAPi.verifyUser(userVerify)
    }

    fun searchResources(
        query: String, resources: String,
        exactMatch: Boolean
    ): Observable<List<SearchedEntity>> {
        return fineractApiManager.searchApi.searchResources(query, resources, exactMatch)
    }

    fun updateClient(clientId: Long, payload: Any): Observable<ResponseBody> {
        return fineractApiManager.clientsApi.updateClient(clientId, payload)
            .map { responseBody -> responseBody }
    }

    fun createSavingsAccount(newAccount: NewAccount?): Observable<GenericResponse> {
        return fineractApiManager.clientsApi.createAccount(newAccount)
    }

    fun getAccounts(clientId: Long): Observable<ClientAccounts> {
        return fineractApiManager.clientsApi.getAccounts(clientId, Constants.SAVINGS)
    }

    val savingsAccounts: Observable<Page<SavingsWithAssociations>>
        get() = fineractApiManager.savingsAccountsApi.getSavingsAccounts(-1)

    fun blockUnblockAccount(accountId: Long, command: String?): Observable<GenericResponse> {
        return fineractApiManager.savingsAccountsApi.blockUnblockAccount(
            accountId,
            command
        )
    }

    fun getClientDetails(clientId: Long): Observable<Client> {
        return fineractApiManager.clientsApi.getClientForId(clientId)
    }

    fun getClientImage(clientId: Long): Observable<ResponseBody> {
        return fineractApiManager.clientsApi.getClientImage(clientId)
    }

    fun addSavedCards(
        clientId: Long,
        card: Card
    ): Observable<GenericResponse> {
        return fineractApiManager.savedCardApi.addSavedCard(clientId.toInt(), card)
    }

    fun fetchSavedCards(clientId: Long): Observable<List<Card>> {
        return fineractApiManager.savedCardApi.getSavedCards(clientId.toInt())
    }

    fun editSavedCard(clientId: Int, card: Card): Observable<GenericResponse> {
        return fineractApiManager.savedCardApi.updateCard(clientId, card.id, card)
    }

    fun deleteSavedCard(clientId: Int, cardId: Int): Observable<GenericResponse> {
        return fineractApiManager.savedCardApi.deleteCard(clientId, cardId)
    }

    fun uploadKYCDocs(
        entityType: String, entityId: Long, name: String,
        desc: String, file: MultipartBody.Part
    ): Observable<GenericResponse> {
        return fineractApiManager.documentApi.createDocument(
            entityType, entityId, name, desc,
            file
        )
    }

    fun getAccountTransfer(transferId: Long): Observable<TransferDetail> {
        return fineractApiManager.accountTransfersApi.getAccountTransfer(transferId)
    }

    fun uploadKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details
    ): Observable<GenericResponse> {
        return fineractApiManager.kycLevel1Api.addKYCLevel1Details(
            clientId,
            kycLevel1Details
        )
    }

    fun fetchKYCLevel1Details(clientId: Int): Observable<List<KYCLevel1Details>> {
        return fineractApiManager.kycLevel1Api.fetchKYCLevel1Details(clientId)
    }

    fun updateKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details
    ): Observable<GenericResponse> {
        return fineractApiManager.kycLevel1Api.updateKYCLevel1Details(
            clientId,
            kycLevel1Details
        )
    }

    fun fetchNotifications(clientId: Long): Observable<List<NotificationPayload>> {
        return fineractApiManager.notificationApi.fetchNotifications(clientId)
    }

    val deliveryMethods: Observable<List<DeliveryMethod>>
        get() = fineractApiManager.twoFactorAuthApi.deliveryMethods

    fun requestOTP(deliveryMethod: String): Observable<String> {
        return fineractApiManager.twoFactorAuthApi.requestOTP(deliveryMethod)
    }

    fun validateToken(token: String): Observable<AccessToken> {
        return fineractApiManager.twoFactorAuthApi.validateToken(token)
    }

    fun getTransactionReceipt(
        outputType: String,
        transactionId: String
    ): Observable<ResponseBody> {
        return fineractApiManager.runReportApi.getTransactionReceipt(
            outputType,
            transactionId
        )
    }

    fun addInvoice(clientId: String, invoice: Invoice?): Observable<GenericResponse> {
        return fineractApiManager.invoiceApi.addInvoice(clientId, invoice)
    }

    fun fetchInvoices(clientId: String): Observable<List<Invoice>> {
        return fineractApiManager.invoiceApi.getInvoices(clientId)
    }

    fun fetchInvoice(clientId: String, invoiceId: String): Observable<List<Invoice>> {
        return fineractApiManager.invoiceApi.getInvoice(clientId, invoiceId)
    }

    fun editInvoice(clientId: String, invoice: Invoice): Observable<GenericResponse> {
        return fineractApiManager.invoiceApi.updateInvoice(clientId, invoice.id, invoice)
    }

    fun deleteInvoice(clientId: String, invoiceId: Int): Observable<GenericResponse> {
        return fineractApiManager.invoiceApi.deleteInvoice(clientId, invoiceId)
    }

    val users: Observable<List<UserWithRole>>
        get() = fineractApiManager.userApi.users

    fun getUser(): Observable<UserWithRole> {
        return fineractApiManager.userApi.getUser()
    }

    fun makeThirdPartyTransfer(transferPayload: TransferPayload): Observable<TPTResponse> {
        return fineractApiManager.thirdPartyTransferApi.makeTransfer(transferPayload)
    }

    fun createStandingInstruction(
        standingInstructionPayload: StandingInstructionPayload
    ): Observable<SDIResponse> {
        return fineractApiManager.standingInstructionApi
            .createStandingInstruction(standingInstructionPayload)
    }

    fun getAllStandingInstructions(clientId: Long): Observable<Page<StandingInstruction>> {
        return fineractApiManager.standingInstructionApi.getAllStandingInstructions(clientId)
    }

    fun getStandingInstruction(standingInstructionId: Long): Observable<StandingInstruction> {
        return fineractApiManager.standingInstructionApi
            .getStandingInstruction(standingInstructionId)
    }

    fun updateStandingInstruction(
        standingInstructionId: Long,
        data: StandingInstructionPayload
    ): Observable<GenericResponse> {
        return fineractApiManager.standingInstructionApi.updateStandingInstruction(
            standingInstructionId, data, "update"
        )
    }

    fun deleteStandingInstruction(standingInstruction: Long): Observable<GenericResponse> {
        return fineractApiManager.standingInstructionApi.deleteStandingInstruction(
            standingInstruction, "delete"
        )
    }

    //self user apis
    fun loginSelf(payload: AuthenticationPayload): Observable<User> {
        return selfApiManager.authenticationApi.authenticate(payload)
    }

    fun getSelfClientDetails(clientId: Long): Observable<Client> {
        return selfApiManager.clientsApi.getClientForId(clientId)
    }

    val selfClientDetails: Observable<Page<Client>>
        get() = selfApiManager.clientsApi.clients

    fun getSelfAccountTransactions(accountId: Long): Observable<SavingsWithAssociations> {
        return selfApiManager.savingAccountsListApi.getSavingsWithAssociations(
            accountId,
            Constants.TRANSACTIONS
        )
    }

    fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long
    ): Observable<Transactions> {
        return selfApiManager.savingAccountsListApi.getSavingAccountTransaction(
            accountId,
            transactionId
        )
    }

    fun getSelfAccounts(clientId: Long): Observable<ClientAccounts> {
        return selfApiManager.clientsApi.getAccounts(clientId, Constants.SAVINGS)
    }

    val beneficiaryList: Observable<List<Beneficiary>>
        get() = selfApiManager.beneficiaryApi.beneficiaryList

    fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): Observable<ResponseBody> {
        return selfApiManager.beneficiaryApi.createBeneficiary(beneficiaryPayload)
    }

    fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload
    ): Observable<ResponseBody> {
        return selfApiManager.beneficiaryApi.updateBeneficiary(beneficiaryId, payload)
    }
}
