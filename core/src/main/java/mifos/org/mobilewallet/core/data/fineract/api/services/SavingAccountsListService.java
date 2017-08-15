package mifos.org.mobilewallet.core.data.fineract.api.services;

import mifos.org.mobilewallet.core.data.fineract.api.ApiEndPoints;
import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
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
