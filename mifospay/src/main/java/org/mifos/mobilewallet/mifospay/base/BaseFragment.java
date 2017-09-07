package org.mifos.mobilewallet.mifospay.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

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
        if (callback != null) {
            callback.setToolbarTitle(title);
        }
    }

    protected void showProgress() {
        if (callback !=  null) {
            callback.showSwipeProgress();
        }
    }

    protected void hideProgress() {
        if (callback != null) {
            callback.hideSwipeProgress();
        }
    }

    protected void replaceFragment(Fragment fragment, int containerId) {

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

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getChildFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (!fragmentPopped && getChildFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }
}
