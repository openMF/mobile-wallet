package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel3Presenter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
public class KYCLevel3Fragment extends BaseFragment implements KYCContract.KYCLevel3View {

    @Inject
    KYCLevel3Presenter mPresenter;

    KYCContract.KYCLevel3Presenter mKYCLevel3Presenter;

    public static KYCLevel3Fragment newInstance() {

        Bundle args = new Bundle();

        KYCLevel3Fragment fragment = new KYCLevel3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(KYCContract.KYCLevel3Presenter presenter) {
        mKYCLevel3Presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_kyc_lvl3, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        //setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_3);

        return rootView;
    }

}
