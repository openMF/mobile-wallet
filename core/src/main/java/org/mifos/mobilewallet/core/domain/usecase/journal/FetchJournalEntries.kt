package org.mifos.mobilewallet.core.domain.usecase.journal

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 19/06/2020
 */
class FetchJournalEntries @Inject constructor(private val apiRepository: FineractRepository) :
        UseCase<FetchJournalEntries.RequestValues, FetchJournalEntries.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.fetchJournalEntry(requestValues.entryIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<JournalEntry>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(journalEntry: JournalEntry)
                            = useCaseCallback.onSuccess(ResponseValue(journalEntry))
                })
    }

    class RequestValues(val entryIdentifier: String) : UseCase.RequestValues

    class ResponseValue(val journalEntry: JournalEntry) : UseCase.ResponseValue

}