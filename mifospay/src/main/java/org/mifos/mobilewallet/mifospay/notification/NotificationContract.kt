package org.mifos.mobilewallet.mifospay.notification

import org.mifos.mobilewallet.core.domain.model.NotificationPayload
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 24/July/2018
 */
interface NotificationContract {
    interface NotificationPresenter : BasePresenter {
        fun fetchNotifications()
    }

    interface NotificationView : BaseView<NotificationPresenter?> {
        fun fetchNotificationsSuccess(notificationPayloadList: List<NotificationPayload>?)
        fun fetchNotificationsError(message: String?)
    }
}