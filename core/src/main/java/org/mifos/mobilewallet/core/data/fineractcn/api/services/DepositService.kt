package org.mifos.mobilewallet.core.data.fineractcn.api.services

import org.mifos.mobilewallet.core.data.fineractcn.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.DepositAccount
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.Product
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

    @GET(ApiEndPoints.DEPOSIT + "/definitions/{productIdentifier}")
    fun fetchProductDetails(
            @Path("productIdentifier") productIdentifier: String): Observable<Product>

}