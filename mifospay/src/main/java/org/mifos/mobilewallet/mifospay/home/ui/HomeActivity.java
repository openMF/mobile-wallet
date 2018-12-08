package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.HomePresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class HomeActivity extends BaseActivity implements HomeContract.HomeView {

    @Inject
    HomePresenter mPresenter;

    HomeContract.HomePresenter mHomePresenter;
    private HomeFragment mHomeFragment;

//    private TextView tvUsername;
//    private ImageView ivUserImage;
//    private TextView tvUseremail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mPresenter.attachView(this);

        setToolbarTitle(Constants.HOME);
        hideBackButton();

        mHomeFragment = HomeFragment.newInstance();
        replaceFragment(mHomeFragment, false, R.id.container);

        swipeLayout.setEnabled(false);

        mHomePresenter.fetchClientDetails();

    }

//    private void setupHeaderView(View headerView) {
//        tvUsername = (TextView) headerView.findViewById(R.id.tv_user_name);
//        tvUseremail = (TextView) headerView.findViewById(R.id.tv_user_email);
//        ivUserImage = (ImageView) headerView.findViewById(R.id.iv_user_image);
//
//        headerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        mHomePresenter.fetchClientDetails();
//
//    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        mHomePresenter = presenter;
    }

    @Override
    public void showClientDetails(Client client) {
//        tvUsername.setText(client.getName());
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(client.getName().substring(0, 1), R.color.colorPrimary);
//        ivUserImage.setImageDrawable(drawable);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = mHomeFragment.getChildFragmentManager()
                .findFragmentById(R.id.bottom_navigation_fragment_container);
        if (fragment != null && !(fragment instanceof WalletFragment) && fragment.isVisible()) {
            mHomeFragment.navigateFragment(R.id.action_home, true);
            return;
        }
        super.onBackPressed();
    }
}
