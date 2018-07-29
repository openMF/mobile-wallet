package org.mifos.mobilewallet.mifospay.notification;

import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by ankur on 24/July/2018
 */

public interface NotificationContract {

    interface NotificationPresenter extends BasePresenter {

        void fetchNotifications();
    }

    interface NotificationView extends BaseView<NotificationPresenter> {

        void fetchNotificationsSuccess(List<NotificationPayload> notificationPayloadList);

        void fetchNotificationsError(String message);
    }
}
