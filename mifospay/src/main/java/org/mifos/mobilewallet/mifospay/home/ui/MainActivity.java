package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.faq.ui.FAQActivity;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.MainPresenter;
import org.mifos.mobilewallet.mifospay.settings.ui.SettingsActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class MainActivity extends BaseActivity implements BaseHomeContract.BaseHomeView {

    @Inject
    MainPresenter mPresenter;

    @Inject
    LocalRepository localRepository;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BaseHomeContract.BaseHomePresenter mHomePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mPresenter.attachView(this);
        mHomePresenter.fetchClientDetails();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        navigateFragment(item.getItemId(), false);
                        return true;
                    }
                });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        setToolbarTitle(getString(R.string.home));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_faq:
                startActivity(new Intent(getApplicationContext(), FAQActivity.class));
                break;
            case R.id.item_profile_setting:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void setPresenter(BaseHomeContract.BaseHomePresenter presenter) {
        mHomePresenter = presenter;
    }

    @Override
    public void showClientDetails(Client client) {
//        tvUserName.setText(client.getName());
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(client.getName().substring(0, 1), R.color.colorPrimary);
//        ivUserImage.setImageDrawable(drawable);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.bottom_navigation_fragment_container);
        if (fragment != null && !(fragment instanceof HomeFragment) && fragment.isVisible()) {
            navigateFragment(R.id.action_home, true);
            return;
        }
        super.onBackPressed();
    }

    private void navigateFragment(int id, boolean shouldSelect) {
        if (shouldSelect) {
            bottomNavigationView.setSelectedItemId(id);
        } else {
            switch (id) {
                case R.id.action_home:
                    replaceFragment(HomeFragment.newInstance(localRepository.getClientDetails()
                                    .getClientId()), false,
                            R.id.bottom_navigation_fragment_container);
                    break;

                case R.id.action_payments:
                    replaceFragment(PaymentsFragment.newInstance(), false,
                            R.id.bottom_navigation_fragment_container);
                    break;

                case R.id.action_finance:
                    replaceFragment(FinanceFragment.newInstance(), false,
                            R.id.bottom_navigation_fragment_container);
                    break;

                case R.id.action_profile:
                    replaceFragment(new ProfileFragment(), false,
                            R.id.bottom_navigation_fragment_container);
                    break;

            }
        }
    }
}
