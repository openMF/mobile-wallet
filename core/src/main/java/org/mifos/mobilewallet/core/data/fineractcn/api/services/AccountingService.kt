package org.mifos.mobilewallet.core.data.fineractcn.api.services

import org.mifos.mobilewallet.core.data.fineractcn.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Devansh on 18/06/2020
 */
interface AccountingService {

    @GET(ApiEndPoints.ACCOUNTING + "/journal")
    fun fetchJournalEntries (
            @Query("account") accountIdentifier: String,
            @Query("dateRange") dateRange: String): Observable<List<JournalEntry>>


    @GET(ApiEndPoints.ACCOUNTING + "/journal/{entryIdentifier}")
    fun fetchJournalEntry (
            @Path("entryIdentifier") entryIdentifier: String): Observable<JournalEntry>
}