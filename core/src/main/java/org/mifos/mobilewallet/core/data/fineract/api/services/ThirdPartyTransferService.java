package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.entity.TPTResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.TransferPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.templates.account.AccountOptionsTemplate;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by dilpreet on 21/6/17.
 */

public interface ThirdPartyTransferService {

    @GET(ApiEndPoints.ACCOUNT_TRANSFER + "/template?type=tpt")
    Observable<AccountOptionsTemplate> getAccountTransferTemplate();

    @POST(ApiEndPoints.ACCOUNT_TRANSFER + "?type=\"tpt\"")
    Observable<TPTResponse> makeTransfer(@Body TransferPayload transferPayload);
}
