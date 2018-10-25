package org.mifos.mobilewallet.mifospay.base;

import android.support.v4.widget.SwipeRefreshLayout;

public interface BaseActivityCallback {

    void showSwipeProgress();

    void hideSwipeProgress();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void setToolbarTitle(String title);

    void setSwipeRefreshEnabled(boolean enabled);

    void showBackButton();

    void hideBackButton();

    SwipeRefreshLayout getSwipeRefreshLayout();
}