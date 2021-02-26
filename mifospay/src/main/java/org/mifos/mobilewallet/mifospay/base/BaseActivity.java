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
 * Created by naman on 16/6/17.
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
        toolbar = findViewById(R.id.toolbar);
        swipeLayout = findViewById(R.id.swipe_layout);
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
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void setSwipeRefreshEnabled(boolean enabled) {
        if (swipeLayout != null) {
            swipeLayout.setEnabled(enabled);
        }
    }

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

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    public void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null && getTitle() != null) {
            setTitle(title);
        }
    }

    @Override
    public void showColoredBackButton(int drawable) {
        showHomeButton();
        setToolbarIcon(drawable);
    }

    @Override
    public void showCloseButton() {
        showHomeButton();
        setToolbarIcon(R.drawable.ic_close);
    }

    private void showHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setToolbarIcon(int res) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(res);
        }
    }

    @Override
    public void hideBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

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

    public void addFragment(Fragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(containerId, fragment);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out);
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

    @Override
    public Class getPassCodeClass() {
        return PassCodeActivity.class;
    }
}
