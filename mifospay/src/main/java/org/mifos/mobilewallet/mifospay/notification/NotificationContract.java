package org.mifos.mobilewallet.mifospay.notification;

import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This is a contract class working as an Interface for UI
 * and Presenter components of the Notification package.
 * @author ankur
 * @since 24/July/2018
 */

public interface NotificationContract {

    /**
     * Defines all the functions in Presenter component.
     */
    interface NotificationPresenter extends BasePresenter {

        void fetchNotifications();
    }

    /**
     * Defines all the functions in UI component.
     */
    interface NotificationView extends BaseView<NotificationPresenter> {

        void fetchNotificationsSuccess(List<NotificationPayload> notificationPayloadList);

        void fetchNotificationsError(String message);
    }
}
