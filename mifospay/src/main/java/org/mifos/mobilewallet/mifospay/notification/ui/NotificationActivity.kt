package org.mifos.mobilewallet.mifospay.notification.ui

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.core.domain.model.NotificationPayload
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.notification.NotificationContract
import org.mifos.mobilewallet.mifospay.notification.NotificationContract.NotificationView
import org.mifos.mobilewallet.mifospay.notification.presenter.NotificationPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * This activity is to view notifications record.
 * Notifications Datatable is populated automatically by server when an event happens.
 * This feature is yet to be implemented on the server side.
 */
class NotificationActivity : BaseActivity(), NotificationView {
    @JvmField
    @Inject
    var mPresenter: NotificationPresenter? = null
    var mNotificationPresenter: NotificationContract.NotificationPresenter? = null

    @JvmField
    @BindView(R.id.rv_notification)
    var mRvNotification: RecyclerView? = null

    @JvmField
    @BindView(R.id.tv_placeholder)
    var tvplaceholder: TextView? = null

    @JvmField
    @Inject
    var mNotificationAdapter: NotificationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        ButterKnife.bind(this)
        setToolbarTitle("Notifications")
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        activityComponent!!.inject(this)
        setupRecyclerView()
        setupSwipeRefreshLayout()
        mPresenter!!.attachView(this)
        showSwipeProgress()
        mNotificationPresenter!!.fetchNotifications()
    }

    private fun setupRecyclerView() {
        mRvNotification!!.layoutManager = LinearLayoutManager(this)
        mRvNotification!!.adapter = mNotificationAdapter
        mRvNotification!!.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setupSwipeRefreshLayout() {
        setSwipeRefreshEnabled(true)
        swipeRefreshLayout!!.setOnRefreshListener { mNotificationPresenter!!.fetchNotifications() }
    }

    override fun setPresenter(presenter: NotificationContract.NotificationPresenter?) {
        mNotificationPresenter = presenter
    }

    override fun fetchNotificationsSuccess(notificationPayloadList: List<NotificationPayload>?) {
        hideSwipeProgress()
        if (notificationPayloadList?.isEmpty() == true) {
            DebugUtil.log("null")
            mRvNotification!!.visibility = View.GONE
            tvplaceholder!!.visibility = View.VISIBLE
        } else {
            DebugUtil.log("yes")
            mRvNotification!!.visibility = View.VISIBLE
            tvplaceholder!!.visibility = View.GONE
            mNotificationAdapter!!.setNotificationPayloadList(notificationPayloadList)
        }
        mNotificationAdapter!!.setNotificationPayloadList(notificationPayloadList)
    }

    override fun fetchNotificationsError(message: String?) {
        hideSwipeProgress()
        showToast(message)
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }
}