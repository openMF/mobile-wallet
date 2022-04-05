package org.mifos.mobilewallet.mifospay.notification.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.notification.FetchNotifications;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.notification.NotificationContract;

import javax.inject.Inject;

/**
 * Created by ankur on 24/July/2018
 */

public class NotificationPresenter implements NotificationContract.NotificationPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    NotificationContract.NotificationView mNotificationView;
    @Inject
    FetchNotifications fetchNotificationsUseCase;

    @Inject
    public NotificationPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mNotificationView = (NotificationContract.NotificationView) baseView;
        mNotificationView.setPresenter(this);
    }

    public void fetchNotifications() {
        mUseCaseHandler.execute(fetchNotificationsUseCase,
                new FetchNotifications.RequestValues(
                        mLocalRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchNotifications.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchNotifications.ResponseValue response) {
                        mNotificationView.fetchNotificationsSuccess(
                                response.getNotificationPayloadList());
                    }

                    @Override
                    public void onError(String message) {
                        mNotificationView.fetchNotificationsError(message);
                    }
                });
    }
}
