package org.mifos.mobilewallet.core.data.fineractcn.api.services

import okhttp3.MultipartBody
import org.mifos.mobilewallet.core.data.fineractcn.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.Customer
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.CustomerPage
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable
import rx.Observable

/**
 * Created by Devansh on 17/06/2020
 */
interface CustomerService {

    @GET(ApiEndPoints.CUSTOMER + "/customers")
    fun fetchCustomers(
            @Query("pageIndex") integer: Int?,
            @Query("size") size: Int?): Observable<CustomerPage>

    @GET(ApiEndPoints.CUSTOMER + "/customers/{identifier}")
    fun fetchCustomer(@Path("identifier") identifier: String): Observable<Customer>

    @POST(ApiEndPoints.CUSTOMER + "/customers")
    fun createCustomer(@Body customer: Customer): Completable

    @PUT(ApiEndPoints.CUSTOMER + "/customers/{identifier}")
    fun updateCustomer(
            @Path("identifier") identifier: String,
            @Body customer: Customer): Completable

    @GET(ApiEndPoints.CUSTOMER + "/customers")
    fun searchCustomer(
            @Query("pageIndex") pageIndex: Int?,
            @Query("size") size: Int?,
            @Query("term") term: String): Observable<CustomerPage>

    @Multipart
    @POST(ApiEndPoints.CUSTOMER + "/customers/{identifier}/portrait")
    fun uploadCustomerPortrait(
            @Path("identifier") customerIdentifier: String,
            @Part file: MultipartBody.Part): Completable

}