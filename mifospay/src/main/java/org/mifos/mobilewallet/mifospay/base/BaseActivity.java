package org.mifos.mobilewallet.mifospay.base;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mifos.mobile.passcode.BasePassCodeActivity;

import org.mifos.mobilewallet.mifospay.MifosPayApp;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.injection.component.ActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.component.DaggerActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;

/**
 * @author naman
 * @since 16/6/17
 */

public class BaseActivity extends BasePassCodeActivity implements BaseActivityCallback {

    public Toolbar toolbar;
    public SwipeRefreshLayout swipeLayout;
    public ProgressDialog progressDialog;
    private ActivityComponent activityComponent;

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(MifosPayApp.get(this).component())
                    .build();
        }
        return activityComponent;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    /**
     * This method shows the swipe Progress
     */
    @Override
    public void showSwipeProgress() {
        if (swipeLayout != null) {
            swipeLayout.setEnabled(true);
            swipeLayout.setRefreshing(true);
        }
    }

    /**
     * This method hides the Swipe Progress.
     */
    @Override
    public void hideSwipeProgress() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
    }

    /**
     * This method enables or disables the swiping to refresh.
     * @param enabled This is a boolean which enables the swiping.
     */
    @Override
    public void setSwipeRefreshEnabled(boolean enabled) {
        if (swipeLayout != null) {
            swipeLayout.setEnabled(enabled);
        }
    }

    /**
     * This method shows the progress dialog
     * @param message This is the message to be shown in the progress dialog.
     */
    @Override
    public void showProgressDialog(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    /**
     * This method hides the progress dialog
     */
    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    /**
     * This method sets the Toolbar title
     * @param title This is the title to be set.
     */
    @Override
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null && getTitle() != null) {
            setTitle(title);
        }
    }

    /**
     * This method shows the back button.
     */
    @Override
    public void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This method hides the back button.
     */
    @Override
    public void hideBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * This method refreshes the layout when swiped down.
     * @return A type of SwipeRefreshLayout
     */
    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeLayout;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method adds the Fragment
     * @param fragment This is the fragment to be added.
     * @param containerId This is the container ID.
     */
    public void addFragment(Fragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(containerId, fragment);
        transaction.commit();
    }

    /**
     * This method replaces the fragment.
     * @param fragment This is the fragment to be replaced with.
     * @param addToBackStack A boolean which tells if added to back stack
     * @param containerId This is the container id
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }

    /**
     * This method clears the Fragment Back Stack
     */
    public void clearFragmentBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * This method return the PassCode class
     * @return A type of Passcode Class
     */
    @Override
    public Class getPassCodeClass() {
        return PassCodeActivity.class;
    }
}
