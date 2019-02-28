package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.ui.HomeActivity;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCDescriptionPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCDescriptionFragment extends
        BaseFragment implements KYCContract.KYCDescriptionView {

    @Inject
    KYCDescriptionPresenter mPresenter;

    KYCContract.KYCDescriptionPresenter mKYCDescriptionPresenter;

    @BindView(R.id.cv_lvl1)
    CardView cvLvl1;

    @BindView(R.id.cv_lvl2)
    CardView cvLvl2;

    @BindView(R.id.cv_lvl3)
    CardView cvLvl3;

    @BindView(R.id.ll_lvl1)
    LinearLayout llLvl1;

    @BindView(R.id.ll_lvl2)
    LinearLayout llLvl2;

    @BindView(R.id.ll_lvl3)
    LinearLayout llLvl3;

    public static KYCDescriptionFragment newInstance() {

        Bundle args = new Bundle();

        KYCDescriptionFragment fragment = new KYCDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(KYCContract.KYCDescriptionPresenter presenter) {
        mKYCDescriptionPresenter = presenter;
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
        mPresenter.attachView(this);
        setToolbarTitle(Constants.COMPLETE_KYC);
        showBackButton();
        setSwipeEnabled(false);

        showProgressDialog(Constants.PLEASE_WAIT);
        mKYCDescriptionPresenter.fetchCurrentLevel();

        return rootView;
    }

    @OnClick(R.id.cv_lvl1)
    public void onLevel1Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel1Fragment.newInstance(), true,
                R.id.container);
    }

    @OnClick(R.id.cv_lvl2)
    public void onLevel2Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel2Fragment.newInstance(), true,
                R.id.container);
    }

    @OnClick(R.id.cv_lvl3)
    public void onLevel3Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel3Fragment.newInstance(), true,
                R.id.container);
    }


    @Override
    public void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details) {
        hideProgressDialog();

        int currentLevel = Integer.parseInt(kycLevel1Details.getCurrentLevel());

        if (currentLevel >= 1) {
            cvLvl1.setClickable(false);
            llLvl1.setBackgroundResource(R.drawable.cardview_round_green);
        }
        if (currentLevel >= 2) {
            cvLvl2.setClickable(false);
            llLvl2.setBackgroundResource(R.drawable.cardview_round_green);
        }
        if (currentLevel >= 3) {
            cvLvl3.setClickable(false);
            llLvl3.setBackgroundResource(R.drawable.cardview_round_green);
        }
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void gotoHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }
}
