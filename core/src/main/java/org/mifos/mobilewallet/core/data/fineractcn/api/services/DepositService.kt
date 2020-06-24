package org.mifos.mobilewallet.core.data.fineractcn.api.services

import org.mifos.mobilewallet.core.data.fineractcn.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineractcn.entity.depositaccount.DepositAccount
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Devansh on 17/06/2020
 */
interface DepositService {

    @GET(ApiEndPoints.DEPOSIT + "/instances")
    fun fetchCustomersDeposits(
            @Query("customer") customerIdentifier: String): Observable<List<DepositAccount>>

    @GET(ApiEndPoints.DEPOSIT + "/instances/{accountIdentifier}")
    fun fetchCustomerDepositDetails(
            @Path("accountIdentifier") accountIdentifier: String): Observable<DepositAccount>
}