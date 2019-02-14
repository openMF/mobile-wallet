package org.mifos.mobilewallet.mifospay.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public interface BaseActivityCallback {

    void showSwipeProgress();

    void hideKeyboard(View view);

    void hideSwipeProgress();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void cancelProgressDialog();

    void setToolbarTitle(String title);

    void setSwipeRefreshEnabled(boolean enabled);

    void showBackButton();

    void showCloseButton();

    void hideBackButton();

    SwipeRefreshLayout getSwipeRefreshLayout();
}