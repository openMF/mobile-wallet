package org.mifospay.core.network.services

import com.mifospay.core.model.domain.NewAccount
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.entity.client.ClientAccounts
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.GenericResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface ClientService {
    @get:GET(ApiEndPoints.CLIENTS)
    val clients: Observable<Page<Client>>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}")
    fun getClientForId(@Path("clientId") clientId: Long): Observable<Client>

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}")
    fun updateClient(
        @Path("clientId") clientId: Long,
        @Body payload: Any
    ): Observable<ResponseBody>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/images")
    fun getClientImage(@Path("clientId") clientId: Long): Observable<ResponseBody>

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}/images")
    fun updateClientImage(
        @Path("clientId") clientId: Long,
        @Part typedFile: MultipartBody.Part?
    ): Observable<GenericResponse>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    fun getClientAccounts(@Path("clientId") clientId: Long): Observable<ClientAccounts>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    fun getAccounts(
        @Path("clientId") clientId: Long,
        @Query("fields") accountType: String
    ): Observable<ClientAccounts>

    @POST(ApiEndPoints.CLIENTS)
    fun <T> createClient(@Body newClient: com.mifospay.core.model.domain.client.NewClient): Observable<T>

    @POST
    fun createAccount(@Body newAccount: NewAccount?): Observable<GenericResponse>
}
