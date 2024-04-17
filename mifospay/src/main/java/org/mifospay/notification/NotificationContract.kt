package org.mifospay.notification

import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

/**
 * Created by ankur on 24/July/2018
 */
interface NotificationContract {
    interface NotificationPresenter : BasePresenter {
        fun fetchNotifications()
    }

    interface NotificationView : BaseView<NotificationPresenter?> {
        fun fetchNotificationsSuccess(notificationPayloadList: List<NotificationPayload?>?)
        fun fetchNotificationsError(message: String?)
    }
}