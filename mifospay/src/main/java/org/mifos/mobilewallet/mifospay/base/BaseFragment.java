package org.mifos.mobilewallet.mifospay.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by naman on 17/6/17.
 */

public class BaseFragment extends Fragment {

    private BaseActivityCallback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method sets the Toolbar title.
     * @param title This is the title to be set.
     */
    protected void setToolbarTitle(String title) {
        if (callback != null) {
            callback.setToolbarTitle(title);
        }
    }

    /**
     * This method shows the back button
     */
    protected void showBackButton() {
        if (callback != null) {
            callback.showBackButton();
        }
    }

    /**
     * This method hides the back button
     */
    protected void hideBackButton() {
        if (callback != null) {
            callback.hideBackButton();
        }
    }

    /**
     * This method shows the swipe progress
     */
    protected void showSwipeProgress() {
        if (callback != null) {
            callback.showSwipeProgress();
        }
    }

    /**
     * This method hides the Swipe progress
     */
    public void hideSwipeProgress() {
        if (callback != null) {
            callback.hideSwipeProgress();
        }
    }

    /**
     * This method shows the progress dialog
     * @param message This is the message to be shown
     */
    protected void showProgressDialog(String message) {
        callback.showProgressDialog(message);
    }

    /**
     * This method hides the progress dialog.
     */
    protected void hideProgressDialog() {
        callback.hideProgressDialog();
    }

    /**
     * This method enables or disables the swiping to refresh.
     * @param enabled This is a boolean which enables the swiping.
     */
    protected void setSwipeEnabled(boolean enabled) {
        if (callback != null) {
            callback.setSwipeRefreshEnabled(enabled);
        }
    }

    /**
     * This method returns the Swipe refresh layout
     * @return A type of Swipe Refresh Layout
     */
    protected SwipeRefreshLayout getSwipeRefreshLayout() {
        if (callback != null) {
            return callback.getSwipeRefreshLayout();
        }
        return null;
    }

    /**
     * This method attaches the context
     * @param context This is the context to be attached
     */
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

    /**
     * This method detaches the context
     */
    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    /**
     * This method replaces the fragment using Fragment Manager
     * @param fragment This is the fragment to be replaced with
     * @param addToBackStack A boolean which tells if added to back stack or not
     * @param containerId This is the container ID.
     */
    public void replaceFragmentUsingFragmentManager(Fragment fragment, boolean addToBackStack,
            int containerId) {
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (!fragmentPopped && getFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }

    /**
     * This method replaces the fragment
     * @param fragment This is the fragment to be replaced with.
     * @param addToBackStack A boolean which tells if added to back stack or not
     * @param containerId This is the container ID
     */
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
