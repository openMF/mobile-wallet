package org.mifos.mobilewallet.data.fineract.api.services;

import org.mifos.mobilewallet.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.data.fineract.entity.accounts.savings.SavingsWithAssociations;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SavingAccountsListService {

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    Observable<SavingsWithAssociations> getSavingsWithAssociations(
            @Path("accountId") long accountId,
            @Query("associations") String associationType);
}
