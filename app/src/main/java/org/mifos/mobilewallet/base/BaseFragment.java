package org.mifos.mobilewallet.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by naman on 17/6/17.
 */

public class BaseFragment extends Fragment {

    private BaseActivityCallback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolbarTitle(String title) {
        callback.setToolbarTitle(title);
    }

    protected void showProgress() {
        callback.showSwipeProgress();
    }

    protected void hideProgress() {
        callback.hideSwipeProgress();
    }

    protected void showProgressDialog(String message) {
        callback.showProgressDialog(message);
    }

    protected void hideProgressDialog() {
        callback.hideProgressDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            callback = (BaseActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseActivityCallback methods");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
