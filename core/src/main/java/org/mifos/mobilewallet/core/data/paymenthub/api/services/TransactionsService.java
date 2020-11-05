package org.mifos.mobilewallet.core.data.paymenthub.api.services;

import org.mifos.mobilewallet.core.data.paymenthub.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction;
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo;
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by naman on 6/4/19
 */

public interface TransactionsService {

    @POST(ApiEndPoints.TRANSFER)
    Observable<TransactionInfo> makeTransaction(@Body Transaction transaction);

    @GET(ApiEndPoints.TRANSFER + "/{transactionId}")
    Observable<TransactionResponse> fetchTransactionInfo(
            @Path("transactionId") String transactionId);

    @POST(ApiEndPoints.TRANSACTION_REQUEST)
    Observable<TransactionInfo> requestTransaction(@Body Transaction transactionRequest);
}
