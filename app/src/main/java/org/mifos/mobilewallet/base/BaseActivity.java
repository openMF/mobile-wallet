package org.mifos.mobilewallet.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.mifos.mobilewallet.MifosWalletApp;
import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.injection.component.ActivityComponent;
import org.mifos.mobilewallet.injection.component.DaggerActivityComponent;
import org.mifos.mobilewallet.injection.module.ActivityModule;

/**
 * Created by naman on 16/6/17.
 */

public class BaseActivity extends AppCompatActivity implements BaseActivityCallback {

    private ActivityComponent activityComponent;

    public Toolbar toolbar;
    public SwipeRefreshLayout swipeLayout;
    public ProgressDialog progressDialog;

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(MifosWalletApp.get(this).component())
                    .build();
        }
        return activityComponent;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public void showSwipeProgress() {
        if (swipeLayout != null) {
            swipeLayout.setEnabled(true);
            swipeLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideSwipeProgress() {
        if (swipeLayout != null) {
            swipeLayout.setEnabled(false);
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void showProgressDialog(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null && getTitle() != null) {
            setTitle(title);
        }
    }

    protected void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    public void clearFragmentBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
