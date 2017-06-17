package org.mifos.mobilewallet.core;

public interface BaseActivityCallback {

    void showSwipeProgress();

    void hideSwipeProgress();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void setToolbarTitle(String title);
}