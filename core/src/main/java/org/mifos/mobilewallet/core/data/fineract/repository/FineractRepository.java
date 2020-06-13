package org.mifos.mobilewallet.core.data.fineract.repository;

import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.api.SelfServiceApiManager;
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.SearchedEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.TPTResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Transactions;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.Beneficiary;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryUpdatePayload;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.TransferPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.register.RegisterPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.register.UserVerify;
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.SDIResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.StandingInstructionPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction;
import org.mifos.mobilewallet.core.domain.model.NewAccount;
import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.core.domain.model.client.NewClient;
import org.mifos.mobilewallet.core.domain.model.twofactor.AccessToken;
import org.mifos.mobilewallet.core.domain.model.twofactor.DeliveryMethod;
import org.mifos.mobilewallet.core.domain.model.user.NewUser;
import org.mifos.mobilewallet.core.domain.usecase.client.CreateClient;
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by naman on 16/6/17.
 */

@Singleton
public class FineractRepository {

    private final FineractApiManager fineractApiManager;
    private final SelfServiceApiManager selfApiManager;

    @Inject
    public FineractRepository(FineractApiManager fineractApiManager) {
        this.fineractApiManager = fineractApiManager;
        this.selfApiManager = FineractApiManager.getSelfApiManager();
    }

    public Observable<CreateClient.ResponseValue> createClient(NewClient newClient) {
        return fineractApiManager.getClientsApi().createClient(newClient);
    }

    public Observable<CreateUser.ResponseValue> createUser(NewUser user) {
        return fineractApiManager.getUserApi().createUser(user);
    }

    public Observable<GenericResponse> updateUser(Object updateUserEntity, int userId) {
        return fineractApiManager.getUserApi().updateUser(userId, updateUserEntity);
    }

    public Observable<ResponseBody> registerUser(RegisterPayload registerPayload) {
        return fineractApiManager.getRegistrationAPi().registerUser(registerPayload);
    }

    public Observable<GenericResponse> deleteUser(int userId) {
        return fineractApiManager.getUserApi().deleteUser(userId);
    }

    public Observable<ResponseBody> verifyUser(UserVerify userVerify) {
        return fineractApiManager.getRegistrationAPi().verifyUser(userVerify);
    }

    public Observable<List<SearchedEntity>> searchResources(String query, String resources,
            Boolean exactMatch) {
        return fineractApiManager.getSearchApi().searchResources(query, resources, exactMatch);
    }

    public Observable<ResponseBody> updateClient(long clientId, Object payload) {
        return fineractApiManager.getClientsApi().updateClient(clientId, payload)
                .map(new Func1<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody call(ResponseBody responseBody) {
                        return responseBody;
                    }
                });
    }

    public Observable<GenericResponse> createSavingsAccount(NewAccount newAccount) {
        return fineractApiManager.getClientsApi().createAccount(newAccount);
    }

    public Observable<ClientAccounts> getAccounts(long clientId) {
        return fineractApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS);
    }

    public Observable<Page<SavingsWithAssociations>> getSavingsAccounts() {
        return fineractApiManager.getSavingAccountsListApi().getSavingsAccounts(-1);
    }

    public Observable<GenericResponse> blockUnblockAccount(long accountId, String command) {
        return fineractApiManager.getSavingAccountsListApi().blockUnblockAccount(accountId,
                command);
    }

    public Observable<Client> getClientDetails(long clientId) {
        return fineractApiManager.getClientsApi().getClientForId(clientId);
    }

    public Observable<ResponseBody> getClientImage(long clientId) {
        return fineractApiManager.getClientsApi().getClientImage(clientId);
    }

    public Observable<GenericResponse> addSavedCards(long clientId,
            Card card) {
        return fineractApiManager.getSavedCardApi().addSavedCard((int) clientId, card);
    }

    public Observable<List<Card>> fetchSavedCards(long clientId) {
        return fineractApiManager.getSavedCardApi().getSavedCards((int) clientId);
    }

    public Observable<GenericResponse> editSavedCard(int clientId, Card card) {
        return fineractApiManager.getSavedCardApi().updateCard(clientId, card.getId(), card);
    }

    public Observable<GenericResponse> deleteSavedCard(int clientId, int cardId) {
        return fineractApiManager.getSavedCardApi().deleteCard(clientId, cardId);
    }

    public Observable<GenericResponse> uploadKYCDocs(String entityType, long entityId, String name,
            String desc, MultipartBody.Part file) {
        return fineractApiManager.getDocumentApi().createDocument(entityType, entityId, name, desc,
                file);
    }

    public Observable<TransferDetail> getAccountTransfer(long transferId) {
        return fineractApiManager.getAccountTransfersApi().getAccountTransfer(transferId);
    }

    public Observable<GenericResponse> uploadKYCLevel1Details(int clientId,
            KYCLevel1Details kycLevel1Details) {
        return fineractApiManager.getKycLevel1Api().addKYCLevel1Details(clientId,
                kycLevel1Details);
    }

    public Observable<List<KYCLevel1Details>> fetchKYCLevel1Details(int clientId) {
        return fineractApiManager.getKycLevel1Api().fetchKYCLevel1Details(clientId);
    }

    public Observable<GenericResponse> updateKYCLevel1Details(int clientId,
            KYCLevel1Details kycLevel1Details) {
        return fineractApiManager.getKycLevel1Api().updateKYCLevel1Details(clientId,
                kycLevel1Details);
    }

    public Observable<List<NotificationPayload>> fetchNotifications(long clientId) {
        return fineractApiManager.getNotificationApi().fetchNotifications(clientId);
    }

    public Observable<List<DeliveryMethod>> getDeliveryMethods() {
        return fineractApiManager.getTwoFactorAuthApi().getDeliveryMethods();
    }

    public Observable<String> requestOTP(String deliveryMethod) {
        return fineractApiManager.getTwoFactorAuthApi().requestOTP(deliveryMethod);
    }

    public Observable<AccessToken> validateToken(String token) {
        return fineractApiManager.getTwoFactorAuthApi().validateToken(token);
    }

    public Observable<ResponseBody> getTransactionReceipt(String outputType,
            String transactionId) {
        return fineractApiManager.getRunReportApi().getTransactionReceipt(outputType,
                transactionId);
    }

    public Observable<GenericResponse> addInvoice(String clientId, Invoice invoice) {
        return fineractApiManager.getInvoiceApi().addInvoice(clientId, invoice);
    }

    public Observable<List<Invoice>> fetchInvoices(String clientId) {
        return fineractApiManager.getInvoiceApi().getInvoices(clientId);
    }

    public Observable<List<Invoice>> fetchInvoice(String clientId, String invoiceId) {
        return fineractApiManager.getInvoiceApi().getInvoice(clientId, invoiceId);
    }


    public Observable<GenericResponse> editInvoice(String clientId, Invoice invoice) {
        return fineractApiManager.getInvoiceApi().updateInvoice(clientId, invoice.getId(), invoice);
    }

    public Observable<GenericResponse> deleteInvoice(String clientId, int invoiceId) {
        return fineractApiManager.getInvoiceApi().deleteInvoice(clientId, invoiceId);
    }

    public Observable<List<UserWithRole>> getUsers() {
        return fineractApiManager.getUserApi().getUsers();
    }

    public Observable<UserWithRole> getUser(long userId) {
        return fineractApiManager.getUserApi().getUser(userId);
    }

    public Observable<TPTResponse> makeThirdPartyTransfer(TransferPayload transferPayload) {
        return fineractApiManager.getThirdPartyTransferApi().makeTransfer(transferPayload);
    }

    public Observable<SDIResponse> createStandingInstruction(
            StandingInstructionPayload standingInstructionPayload) {
        return fineractApiManager.getStandingInstructionApi()
                .createStandingInstruction(standingInstructionPayload);
    }

    public Observable<Page<StandingInstruction>> getAllStandingInstructions(long clientId) {
        return fineractApiManager.getStandingInstructionApi().getAllStandingInstructions(clientId);
    }

    public Observable<StandingInstruction> getStandingInstruction(long standingInstructionId) {
        return fineractApiManager.getStandingInstructionApi()
                .getStandingInstruction(standingInstructionId);
    }

    public Observable<GenericResponse> updateStandingInstruction(long standingInstructionId,
                              StandingInstructionPayload standingInstructionPayload) {
        return fineractApiManager.getStandingInstructionApi().updateStandingInstruction(
                standingInstructionId, standingInstructionPayload, "update");
    }

    public Observable<GenericResponse> deleteStandingInstruction(long standingInstruction) {
        return fineractApiManager.getStandingInstructionApi().deleteStandingInstruction(
                standingInstruction, "delete");
    }

    //self user apis

    public Observable<UserEntity> loginSelf(String username, String password) {
        return selfApiManager.getAuthenticationApi().authenticate(username, password);
    }

    public Observable<Client> getSelfClientDetails(long clientId) {
        return selfApiManager.getClientsApi().getClientForId(clientId);
    }

    public Observable<Page<Client>> getSelfClientDetails() {
        return selfApiManager.getClientsApi().getClients();
    }

    public Observable<SavingsWithAssociations> getSelfAccountTransactions(long accountId) {
        return selfApiManager
                .getSavingAccountsListApi().getSavingsWithAssociations(accountId,
                        Constants.TRANSACTIONS);
    }

    public Observable<Transactions> getSelfAccountTransactionFromId(long accountId,
                                                                    long transactionId) {
        return selfApiManager
                .getSavingAccountsListApi().getSavingAccountTransaction(accountId,
                        transactionId);
    }

    public Observable<ClientAccounts> getSelfAccounts(long clientId) {
        return selfApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS);
    }

    public Observable<List<Beneficiary>> getBeneficiaryList() {
        return selfApiManager.getBeneficiaryApi().getBeneficiaryList();
    }

    public Observable<ResponseBody> createBeneficiary(BeneficiaryPayload beneficiaryPayload) {
        return selfApiManager.getBeneficiaryApi().createBeneficiary(beneficiaryPayload);
    }

    public Observable<ResponseBody> updateBeneficiary(long beneficiaryId,
            BeneficiaryUpdatePayload payload) {
        return selfApiManager.getBeneficiaryApi().updateBeneficiary(beneficiaryId, payload);
    }
}
