package org.mifos.mobilewallet.core.data.fineract.repository;

import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.api.SelfServiceApiManager;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.SearchedEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.Beneficiary;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryUpdatePayload;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.TransferPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.UpdateVpaPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.register.RegisterPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.register.UserVerify;
import org.mifos.mobilewallet.core.domain.model.Card;
import org.mifos.mobilewallet.core.domain.model.KYCLevel1Details;
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

    public Observable<ResponseBody> registerUser(RegisterPayload registerPayload) {
        return fineractApiManager.getRegistrationAPi().registerUser(registerPayload);
    }

    public Observable<ResponseBody> verifyUser(UserVerify userVerify) {
        return fineractApiManager.getRegistrationAPi().verifyUser(userVerify);
    }

    public Observable<List<SearchedEntity>> searchResources(String query, String resources,
            Boolean exactMatch) {
        return fineractApiManager.getSearchApi().searchResources(query, resources, exactMatch);
    }


    public Observable<ResponseBody> updateClientVpa(long clientId, UpdateVpaPayload payload) {
        return fineractApiManager.getClientsApi().updateClientVpa(clientId, payload)
                .map(new Func1<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody call(ResponseBody responseBody) {
                        return responseBody;
                    }
                });
    }

    public Observable<ClientAccounts> getAccounts(long clientId) {
        return fineractApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS);
    }

    public Observable<Client> getClientDetails(long clientId) {
        return fineractApiManager.getClientsApi().getClientForId(clientId);
    }

    public Observable<GenericResponse> addSavedCards(long clientId,
            Card card) {
        return fineractApiManager.getDatatablesApi().addSavedCard((int) clientId, card);
    }

    public Observable<List<Card>> fetchSavedCards(long clientId) {
        return fineractApiManager.getDatatablesApi().getSavedCards((int) clientId);
    }

    public Observable<GenericResponse> editSavedCard(int clientId, Card card) {
        return fineractApiManager.getDatatablesApi().updateCard(clientId, card.getId(), card);
    }

    public Observable<GenericResponse> deleteSavedCard(int clientId, int cardId) {
        return fineractApiManager.getDatatablesApi().deleteCard(clientId, cardId);
    }

    public Observable<GenericResponse> uploadKYCDocs(String entityType, long entityId, String name,
            String desc, MultipartBody.Part file) {

        return fineractApiManager.getDocumentApi().createDocument(entityType, entityId, name, desc,
                file);

//        return fineractApiManager.getDocumentApi().createDocument(entityType, entityId, name,
// desc,
//                file)
//                .map(new Func1<ResponseBody, ResponseBody>() {
//                    @Override
//                    public ResponseBody call(ResponseBody responseBody) {
//                        return responseBody;
//                    }
//                });
    }

    public Observable<GenericResponse> uploadKYCLevel1Details(int clientId,
            KYCLevel1Details kycLevel1Details) {
        return fineractApiManager.getDatatablesApi().addKYCLevel1Details(clientId,
                kycLevel1Details);
    }

    public Observable<List<KYCLevel1Details>> fetchKYCLevel1Details(int clientId) {
        return fineractApiManager.getDatatablesApi().fetchKYCLevel1Details(clientId);
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

    public Observable<ResponseBody> makeThirdPartyTransfer(TransferPayload transferPayload) {
        return selfApiManager.getThirdPartyTransferApi().makeTransfer(transferPayload);
    }
}
