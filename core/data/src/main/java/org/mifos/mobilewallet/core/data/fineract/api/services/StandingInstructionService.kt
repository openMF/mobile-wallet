package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import org.mifos.mobilewallet.core.data.fineract.entity.Page
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.SDIResponse
import org.mifos.mobilewallet.core.data.fineract.entity.payload.StandingInstructionPayload
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface StandingInstructionService {

    @POST(ApiEndPoints.STANDING_INSTRUCTION)
    fun createStandingInstruction(@Body standingInstructionPayload: StandingInstructionPayload) :
            Observable<SDIResponse>

    /**
     * @param clientId - passed as Query to limit the response to client specific response
     */
    @GET(ApiEndPoints.STANDING_INSTRUCTION)
    fun getAllStandingInstructions(@Query("clientId") clientId : Long)
            : Observable<Page<StandingInstruction>>

    @GET("${ApiEndPoints.STANDING_INSTRUCTION}/{standingInstructionId}")
    fun getStandingInstruction(@Path("standingInstructionId") standingInstructionId: Long)
            : Observable<StandingInstruction>

    /**
     * @param command - if command is passed as "update" then the corresponding standing instruction
     *                  is updated. If passed as "delete" then the standing instruction is deleted.
     *
     * @param standingInstructionId - unique id of the standing instruction on which the operation
     *                                will be performed.
     */
    @PUT("${ApiEndPoints.STANDING_INSTRUCTION}/{standingInstructionId}")
    fun deleteStandingInstruction(
            @Path("standingInstructionId") standingInstructionId: Long,
            @Query("command") command: String): Observable<GenericResponse>

    @PUT("${ApiEndPoints.STANDING_INSTRUCTION}/{standingInstructionId}")
    fun updateStandingInstruction(
            @Path("standingInstructionId") standingInstructionId: Long,
            @Body standingInstructionPayload: StandingInstructionPayload,
            @Query("command") command: String): Observable<GenericResponse>

}