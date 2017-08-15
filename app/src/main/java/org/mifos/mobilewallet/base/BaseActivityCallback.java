package org.mifos.mobilewallet.base;

public interface BaseActivityCallback {

    void showSwipeProgress();

    void hideSwipeProgress();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void setToolbarTitle(String title);
}