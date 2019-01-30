package org.mifos.mobilewallet.mifospay.notification.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.notification.FetchNotifications;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.notification.NotificationContract;

import javax.inject.Inject;


/**
 * This class is the Presenter component of the notification package.
 * @author ankur
 * @since 24/July/2018
 */

public class NotificationPresenter implements NotificationContract.NotificationPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    NotificationContract.NotificationView mNotificationView;
    @Inject
    FetchNotifications fetchNotificationsUseCase;

    /**
     * Constructor for class NotificationPresenter which is used to initialize
     * the objects that are passed as arguments.
     * @param useCaseHandler  : An object of UseCaseHandler
     * @param localRepository : An object of LocalRepository
     */
    @Inject
    public NotificationPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    /**
     * Attaches View to the Presenter.
     */
    @Override
    public void attachView(BaseView baseView) {
        mNotificationView = (NotificationContract.NotificationView) baseView;
        mNotificationView.setPresenter(this);
    }


    /**
     * A function to fetch notifications.
     */
    public void fetchNotifications() {
        mUseCaseHandler.execute(fetchNotificationsUseCase,
                new FetchNotifications.RequestValues(
                        mLocalRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchNotifications.ResponseValue>() {

                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(FetchNotifications.ResponseValue response) {
                        mNotificationView.fetchNotificationsSuccess(
                                response.getNotificationPayloadList());
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mNotificationView.fetchNotificationsError(message);
                    }
                });
    }
}
