package org.mifos.mobilewallet.mifospay.notification.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.notification.NotificationContract;
import org.mifos.mobilewallet.mifospay.notification.presenter.NotificationPresenter;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity is to view notifications record.
 * Notifications Datatable is populated automatically by server when an event happens.
 * This feature is yet to be implemented on the server side.
 */

public class NotificationActivity extends BaseActivity implements
        NotificationContract.NotificationView {

    @Inject
    NotificationPresenter mPresenter;
    NotificationContract.NotificationPresenter mNotificationPresenter;

    @BindView(R.id.rv_notification)
    RecyclerView mRvNotification;

    @Inject
    NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setToolbarTitle("Notifications");
        showBackButton();
        getActivityComponent().inject(this);

        setupRecyclerView();
        setupSwipeRefreshLayout();

        mPresenter.attachView(this);

        showSwipeProgress();
        mNotificationPresenter.fetchNotifications();
    }

    private void setupRecyclerView() {
        mRvNotification.setLayoutManager(new LinearLayoutManager(this));
        mRvNotification.setAdapter(mNotificationAdapter);
        mRvNotification.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void setupSwipeRefreshLayout() {
        setSwipeRefreshEnabled(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNotificationPresenter.fetchNotifications();
            }
        });
    }

    @Override
    public void setPresenter(NotificationContract.NotificationPresenter presenter) {
        mNotificationPresenter = presenter;
    }

    @Override
    public void fetchNotificationsSuccess(List<NotificationPayload> notificationPayloadList) {
        hideSwipeProgress();
        mNotificationAdapter.setNotificationPayloadList(notificationPayloadList);
    }

    @Override
    public void fetchNotificationsError(String message) {
        hideSwipeProgress();
        showToast(message);
    }

    public void showToast(String message) {
        Toaster.showToast(this, message);
    }
}