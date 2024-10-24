/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.fineract.repository

import com.mifospay.core.model.domain.NewAccount
import com.mifospay.core.model.domain.NotificationPayload
import com.mifospay.core.model.domain.client.NewClient
import com.mifospay.core.model.domain.twofactor.AccessToken
import com.mifospay.core.model.domain.twofactor.DeliveryMethod
import com.mifospay.core.model.domain.user.NewUser
import com.mifospay.core.model.domain.user.User
import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.SearchedEntity
import com.mifospay.core.model.entity.TPTResponse
import com.mifospay.core.model.entity.UserWithRole
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import com.mifospay.core.model.entity.accounts.savings.Transactions
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import com.mifospay.core.model.entity.authentication.AuthenticationPayload
import com.mifospay.core.model.entity.beneficary.Beneficiary
import com.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import com.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.entity.client.ClientAccounts
import com.mifospay.core.model.entity.invoice.Invoice
import com.mifospay.core.model.entity.invoice.InvoiceEntity
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import com.mifospay.core.model.entity.payload.StandingInstructionPayload
import com.mifospay.core.model.entity.payload.TransferPayload
import com.mifospay.core.model.entity.register.RegisterPayload
import com.mifospay.core.model.entity.register.UserVerify
import com.mifospay.core.model.entity.savedcards.Card
import com.mifospay.core.model.entity.standinginstruction.SDIResponse
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.mifospay.core.data.domain.usecase.client.CreateClient
import org.mifospay.core.data.domain.usecase.user.CreateUser
import org.mifospay.core.data.util.Constants
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.GenericResponse
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.services.KtorAuthenticationService
import rx.Observable

@Suppress("TooManyFunctions")
class FineractRepository(
    private val fineractApiManager: FineractApiManager,
    private val selfApiManager: SelfServiceApiManager,
    private val ktorAuthenticationService: KtorAuthenticationService,
) {
    fun createClient(newClient: NewClient): Observable<CreateClient.ResponseValue> {
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
        query: String,
        resources: String,
        exactMatch: Boolean,
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

    suspend fun savingsAccounts(): Page<SavingsWithAssociations> =
        fineractApiManager.ktorSavingsAccountApi.getSavingsAccounts(-1)

    suspend fun blockUnblockAccount(accountId: Long, command: String?): GenericResponse {
        return fineractApiManager.ktorSavingsAccountApi.blockUnblockAccount(
            accountId,
            command,
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
        card: Card,
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
        entityType: String,
        entityId: Long,
        name: String,
        desc: String,
        file: MultipartBody.Part,
    ): Observable<GenericResponse> {
        return fineractApiManager.documentApi.createDocument(
            entityType,
            entityId,
            name,
            desc,
            file,
        )
    }

    fun getAccountTransfer(transferId: Long): Observable<TransferDetail> {
        return fineractApiManager.accountTransfersApi.getAccountTransfer(transferId)
    }

    fun uploadKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Observable<GenericResponse> {
        return fineractApiManager.kycLevel1Api.addKYCLevel1Details(
            clientId,
            kycLevel1Details,
        )
    }

    fun fetchKYCLevel1Details(clientId: Int): Observable<List<KYCLevel1Details>> {
        return fineractApiManager.kycLevel1Api.fetchKYCLevel1Details(clientId)
    }

    fun updateKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Observable<GenericResponse> {
        return fineractApiManager.kycLevel1Api.updateKYCLevel1Details(
            clientId,
            kycLevel1Details,
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
        transactionId: String,
    ): Observable<ResponseBody> {
        return fineractApiManager.runReportApi.getTransactionReceipt(
            outputType,
            transactionId,
        )
    }

    fun addInvoice(clientId: Long, invoice: InvoiceEntity?): Observable<Unit> {
        return Observable.fromCallable {
            fineractApiManager.invoiceApi.addInvoice(clientId, invoice)
        }
    }

    fun fetchInvoices(clientId: Long): Observable<List<Invoice>> {
        return fineractApiManager.invoiceApi.getInvoices(clientId)
    }

    fun fetchInvoice(clientId: Long, invoiceId: Long): Observable<List<Invoice>> {
        return fineractApiManager.invoiceApi.getInvoice(clientId, invoiceId)
    }

    fun editInvoice(clientId: Long, invoice: Invoice): Observable<Unit> {
        return Observable.fromCallable {
            fineractApiManager.invoiceApi.updateInvoice(clientId, invoice.id, invoice)
        }
    }

    fun deleteInvoice(clientId: Long, invoiceId: Long): Observable<Unit> {
        return Observable.fromCallable {
            fineractApiManager.invoiceApi.deleteInvoice(clientId, invoiceId)
        }
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
        standingInstructionPayload: StandingInstructionPayload,
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
        data: StandingInstructionPayload,
    ): Observable<GenericResponse> {
        return fineractApiManager.standingInstructionApi.updateStandingInstruction(
            standingInstructionId,
            data,
            "update",
        )
    }

    fun deleteStandingInstruction(standingInstruction: Long): Observable<GenericResponse> {
        return fineractApiManager.standingInstructionApi.deleteStandingInstruction(
            standingInstruction,
            "delete",
        )
    }

    // self user apis
    suspend fun loginSelf(payload: AuthenticationPayload): User {
        return ktorAuthenticationService.authenticate(payload)
    }

    fun getSelfClientDetails(clientId: Long): Observable<Client> {
        return selfApiManager.clientsApi.getClientForId(clientId)
    }

    val selfClientDetails: Observable<Page<Client>>
        get() = selfApiManager.clientsApi.clients

    suspend fun getSelfAccountTransactions(accountId: Long): SavingsWithAssociations {
        return selfApiManager.ktorSavingsAccountApi.getSavingsWithAssociations(
            accountId,
            Constants.TRANSACTIONS,
        )
    }

    suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): Transactions {
        return selfApiManager.ktorSavingsAccountApi.getSavingAccountTransaction(
            accountId,
            transactionId,
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
        payload: BeneficiaryUpdatePayload,
    ): Observable<ResponseBody> {
        return selfApiManager.beneficiaryApi.updateBeneficiary(beneficiaryId, payload)
    }
}
