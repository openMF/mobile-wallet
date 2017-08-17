package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mifos.org.mobilewallet.core.data.local.LocalRepository;

/**
 * Created by naman on 17/8/17.
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Inject
    LocalRepository localRepository;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container, false);

        ButterKnife.bind(this, rootView);

        setToolbarTitle("Wallet");

        ((BaseActivity) getActivity()).
                replaceFragment(WalletFragment.newInstance(localRepository
                                .getCurrentClientId()), false,
                        R.id.bottom_navigation_fragment_container);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_wallet:
                                ((BaseActivity) getActivity()).
                                        replaceFragment(WalletFragment.newInstance(localRepository
                                                        .getCurrentClientId()), false,
                                                R.id.bottom_navigation_fragment_container);
                                break;

                            case R.id.action_send:

                            case R.id.action_profile:

                        }
                        return true;
                    }
                });


        return rootView;
    }
}
