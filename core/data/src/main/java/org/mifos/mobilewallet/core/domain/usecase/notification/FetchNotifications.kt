package org.mifos.mobilewallet.core.domain.usecase.notification

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import com.mifos.mobilewallet.model.domain.NotificationPayload
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 24/July/2018
 */
class FetchNotifications @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchNotifications.RequestValues, FetchNotifications.ResponseValue>() {

    class RequestValues(val clientId: Long) : UseCase.RequestValues
    class ResponseValue(
        val notificationPayloadList: List<NotificationPayload?>
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {

        mFineractRepository.fetchNotifications(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<NotificationPayload?>?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_NOTIFICATIONS)
                }

                override fun onNext(notificationPayloads: List<NotificationPayload?>?) {
                    useCaseCallback.onSuccess(notificationPayloads?.let { ResponseValue(it) })
                }
            })

    }
}
