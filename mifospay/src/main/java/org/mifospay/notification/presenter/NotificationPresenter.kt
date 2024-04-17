package org.mifospay.notification.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.notification.FetchNotifications
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.notification.NotificationContract
import org.mifospay.notification.NotificationContract.NotificationView
import javax.inject.Inject

/**
 * Created by ankur on 24/July/2018
 */
class NotificationPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val fetchNotificationsUseCase: FetchNotifications
) : NotificationContract.NotificationPresenter {
    var mNotificationView: NotificationView? = null

    override fun attachView(baseView: BaseView<*>?) {
        mNotificationView = baseView as NotificationView?
        mNotificationView?.setPresenter(this)
    }

    override fun fetchNotifications() {
        mUseCaseHandler.execute(fetchNotificationsUseCase,
            FetchNotifications.RequestValues(
                mLocalRepository.clientDetails.clientId
            ),
            object : UseCaseCallback<FetchNotifications.ResponseValue> {
                override fun onSuccess(response: FetchNotifications.ResponseValue) {
                    mNotificationView?.fetchNotificationsSuccess(
                        response.notificationPayloadList
                    )
                }

                override fun onError(message: String) {
                    mNotificationView?.fetchNotificationsError(message)
                }
            })
    }
}