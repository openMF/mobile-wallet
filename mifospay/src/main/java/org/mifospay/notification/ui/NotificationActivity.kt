package org.mifospay.notification.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.notification.NotificationContract
import org.mifospay.notification.NotificationContract.NotificationView
import org.mifospay.notification.presenter.NotificationPresenter
import org.mifospay.common.Constants
import org.mifospay.utils.DebugUtil
import org.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * This activity is to view notifications record.
 * Notifications Datatable is populated automatically by server when an event happens.
 * This feature is yet to be implemented on the server side.
 */
@AndroidEntryPoint
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
        showColoredBackButton(R.drawable.ic_arrow_back_black_24dp)
        setupRecyclerView()
        setupSwipeRefreshLayout()
        mPresenter?.attachView(this)
        showSwipeProgress()
        mNotificationPresenter?.fetchNotifications()
    }

    private fun setupRecyclerView() {
        mRvNotification?.layoutManager = LinearLayoutManager(this)
        mRvNotification?.adapter = mNotificationAdapter
        mRvNotification?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setupSwipeRefreshLayout() {
        setSwipeRefreshEnabled(true)
        swipeRefreshLayout?.setOnRefreshListener { mNotificationPresenter?.fetchNotifications() }
    }

    override fun fetchNotificationsSuccess(notificationPayloadList: List<NotificationPayload?>?) {
        hideSwipeProgress()
        if (notificationPayloadList.isNullOrEmpty()) {
            DebugUtil.log("null")
            mRvNotification?.visibility = View.GONE
            tvplaceholder?.visibility = View.VISIBLE
        } else {
            DebugUtil.log("yes")
            mRvNotification?.visibility = View.VISIBLE
            tvplaceholder?.visibility = View.GONE
            mNotificationAdapter?.setNotificationPayloadList(notificationPayloadList as List<NotificationPayload>)
        }
        mNotificationAdapter?.setNotificationPayloadList(notificationPayloadList as List<NotificationPayload>)
    }

    override fun fetchNotificationsError(message: String?) {
        hideSwipeProgress()
        showToast(message)
    }

    override fun setPresenter(presenter: NotificationContract.NotificationPresenter?) {
        mNotificationPresenter = presenter
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }
}