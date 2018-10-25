package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, rootView);

        setToolbarTitle(Constants.HOME);
        hideBackButton();

        replaceFragment(WalletFragment.newInstance(localRepository
                        .getClientDetails().getClientId()), false,
                R.id.bottom_navigation_fragment_container);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        navigateFragment(item.getItemId(), false);
                        return true;
                    }
                });

        return rootView;
    }

    public void navigateFragment(int id, boolean shouldSelect) {
        if (shouldSelect) {
            bottomNavigationView.setSelectedItemId(id);
        } else {
            switch (id) {
                case R.id.action_home:
                    replaceFragment(WalletFragment.newInstance(localRepository.getClientDetails()
                                    .getClientId()), false,
                            R.id.bottom_navigation_fragment_container);
                    break;

                case R.id.action_transfer:
                    replaceFragment(new TransferFragment(), false,
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
