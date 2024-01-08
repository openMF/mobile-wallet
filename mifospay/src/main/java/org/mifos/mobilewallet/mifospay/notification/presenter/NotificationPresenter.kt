package org.mifos.mobilewallet.mifospay.notification.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.notification.FetchNotifications
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.notification.NotificationContract
import org.mifos.mobilewallet.mifospay.notification.NotificationContract.NotificationView
import javax.inject.Inject

/**
 * Created by ankur on 24/July/2018
 */
class NotificationPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : NotificationContract.NotificationPresenter {
    var mNotificationView: NotificationView? = null

    @JvmField
    @Inject
    var fetchNotificationsUseCase: FetchNotifications? = null
    override fun attachView(baseView: BaseView<*>?) {
        mNotificationView = baseView as NotificationView?
        mNotificationView?.setPresenter(this)
    }

    override fun fetchNotifications() {
        mUseCaseHandler.execute(fetchNotificationsUseCase,
            FetchNotifications.RequestValues(
                mLocalRepository.clientDetails.clientId
            ),
            object : UseCaseCallback<FetchNotifications.ResponseValue?> {
                override fun onSuccess(response: FetchNotifications.ResponseValue?) {
                    mNotificationView?.fetchNotificationsSuccess(
                        response?.notificationPayloadList
                    )
                }

                override fun onError(message: String) {
                    mNotificationView?.fetchNotificationsError(message)
                }
            })
    }
}