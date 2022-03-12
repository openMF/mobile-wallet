package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Transactions;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SavingsAccountsService {

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    Observable<SavingsWithAssociations> getSavingsWithAssociations(
            @Path("accountId") long accountId,
            @Query("associations") String associationType);

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS)
    Observable<Page<SavingsWithAssociations>> getSavingsAccounts(
            @Query("limit") int limit);

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS)
    Observable<GenericResponse> createSavingsAccount(@Body SavingAccount savingAccount);

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    Observable<GenericResponse> blockUnblockAccount(
            @Path("accountId") long accountId,
            @Query("command") String command);

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}/" + ApiEndPoints.TRANSACTIONS
            + "/{transactionId}")
    Observable<Transactions> getSavingAccountTransaction(@Path("accountId") long accountId,
                                                         @Path("transactionId") long transactionId);
}
