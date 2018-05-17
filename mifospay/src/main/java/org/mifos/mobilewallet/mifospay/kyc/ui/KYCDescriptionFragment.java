package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCDescriptionFragment extends BaseFragment {

    @BindView(R.id.cv_lvl1)
    CardView cvLvl1;

    @BindView(R.id.cv_lvl2)
    CardView cvLvl2;

    @BindView(R.id.cv_lvl3)
    CardView cvLvl3;

    public static KYCDescriptionFragment newInstance() {

        Bundle args = new Bundle();

        KYCDescriptionFragment fragment = new KYCDescriptionFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_kyc_desc, container, false);
        ButterKnife.bind(this, rootView);

        setToolbarTitle("KYC Registration");

        return rootView;
    }

    @OnClick(R.id.cv_lvl1)
    public void onLevel1Clicked() {
        replaceFragment(KYCLevel1Fragment.newInstance(), R.id.container_kyc);
    }

    @OnClick(R.id.cv_lvl2)
    public void onLevel2Clicked() {
        replaceFragment(KYCLevel2Fragment.newInstance(), R.id.container_kyc);
    }

    @OnClick(R.id.cv_lvl3)
    public void onLevel3Clicked() {
        replaceFragment(KYCLevel3Fragment.newInstance(), R.id.container_kyc);
    }

    protected void replaceFragment(Fragment fragment, int containerId) {

        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && getFragmentManager().findFragmentByTag(backStateName) == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container_kyc, fragment, backStateName);
            transaction.addToBackStack(backStateName);
            transaction.commit();
        }
    }
}
