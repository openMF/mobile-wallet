package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ankur on 06/June/2018
 */

public interface RunReportService {

    @GET(ApiEndPoints.RUN_REPORT + "/Savings Transaction Receipt")
    Observable<ResponseBody> getTransactionReceipt(
            @Query("output-type") String outputType,
            @Query("R_transactionId") String R_transactionId
    );
}
