package org.mifos.mobilewallet.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.core.BaseActivity;
import org.mifos.mobilewallet.home.HomeContract;
import org.mifos.mobilewallet.home.HomePresenter;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;
import org.mifos.mobilewallet.invoice.ui.InvoiceFragment;
import org.mifos.mobilewallet.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.user.ui.UserDetailsActivity;
import org.mifos.mobilewallet.utils.TextDrawable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class HomeActivity extends BaseActivity implements HomeContract.HomeView {

    @Inject
    HomePresenter mPresenter;

    HomeContract.HomePresenter mHomePresenter;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;

    private TextView tvUsername;
    private ImageView ivUserImage;
    private TextView tvUseremail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mPresenter.attachView(this);

        setToolbarTitle("Home");

        replaceFragment(InvoiceFragment.newInstance(), false, R.id.container);
        setupDrawerContent(navigationView);
        swipeLayout.setEnabled(false);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
        setupHeaderView(navigationView.getHeaderView(0));
        mHomePresenter.fetchWalletBalance();

    }


    private void updatePosition(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_home:
                break;
            case R.id.item_qr:
                startActivity(new Intent(this, ShowQrActivity.class));
                break;
        }
    }

    private void setupHeaderView(View headerView) {
        tvUsername = (TextView) headerView.findViewById(R.id.tv_user_name);
        tvUseremail = (TextView) headerView.findViewById(R.id.tv_user_email);
        ivUserImage = (ImageView) headerView.findViewById(R.id.iv_user_image);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UserDetailsActivity.class));
            }
        });

        mHomePresenter.fetchClientDetails();

    }

    @Override
    public void showWalletBalance(int amount) {
        TextView counterText = (TextView) ((FrameLayout)
                navigationView.getMenu().findItem(R.id.item_wallet).getActionView()).getChildAt(0);
        counterText.setText("₹" + amount);

    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        mHomePresenter = presenter;
    }

    @Override
    public void showUserDetailsHeader(ClientDetails clientDetails) {
        tvUsername.setText(clientDetails.getName());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(clientDetails.getName().substring(0, 1), R.color.colorPrimary);
        ivUserImage.setImageDrawable(drawable);

    }
}
