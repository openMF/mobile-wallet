package org.mifos.mobilewallet.mifospay.base;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * This interface is used for fragment to fragment communication.
 */
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