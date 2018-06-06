package org.mifos.mobilewallet.core.domain.usecase.notification;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 24/July/2018
 */

public class FetchNotifications extends
        UseCase<FetchNotifications.RequestValues, FetchNotifications.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchNotifications(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.fetchNotifications(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<NotificationPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_NOTIFICATIONS);
                    }

                    @Override
                    public void onNext(List<NotificationPayload> notificationPayloads) {
                        getUseCaseCallback().onSuccess(new ResponseValue(notificationPayloads));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final long clientId;

        public RequestValues(long clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<NotificationPayload> mNotificationPayloadList;

        public ResponseValue(
                List<NotificationPayload> notificationPayloadList) {
            mNotificationPayloadList = notificationPayloadList;
        }

        public List<NotificationPayload> getNotificationPayloadList() {
            return mNotificationPayloadList;
        }
    }
}
