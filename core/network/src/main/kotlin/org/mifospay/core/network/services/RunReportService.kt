package org.mifospay.core.network.services

import okhttp3.ResponseBody
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by ankur on 06/June/2018
 */
@Suppress("FunctionParameterNaming")
interface RunReportService {
    @GET(ApiEndPoints.RUN_REPORT + "/Savings Transaction Receipt")
    fun getTransactionReceipt(
        @Query("output-type") outputType: String,
        @Query("R_transactionId") R_transactionId: String
    ): Observable<ResponseBody>
}
