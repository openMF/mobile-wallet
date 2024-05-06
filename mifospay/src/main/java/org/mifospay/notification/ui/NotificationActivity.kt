package org.mifospay.notification.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.compose.setContent
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
import org.mifospay.theme.MifosTheme
import org.mifospay.utils.DebugUtil
import org.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * This activity is to view notifications record.
 * Notifications Datatable is populated automatically by server when an event happens.
 * This feature is yet to be implemented on the server side.
 */
@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                NotificationScreen()
            }
        }
//        mNotificationPresenter?.fetchNotifications()
    }

//    private fun setupSwipeRefreshLayout() {
//        setSwipeRefreshEnabled(true)
//        swipeRefreshLayout?.setOnRefreshListener { mNotificationPresenter?.fetchNotifications() }
//    }

}