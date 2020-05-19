package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.ui.MainActivity;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCDescriptionPresenter;
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

    @BindView(R.id.lv1)
    LinearLayout lv1;

    @BindView(R.id.lv2)
    LinearLayout lv2;

    @BindView(R.id.lv3)
    LinearLayout lv3;

    @BindView(R.id.blv1)
    Button blv1;

    @BindView(R.id.blv2)
    Button blv2;

    @BindView(R.id.blv3)
    Button blv3;

    @BindView(R.id.completedtx1)
    TextView completedtxt1;

    @BindView(R.id.completedtx2)
    TextView completedtxt2;

    @BindView(R.id.completedtx3)
    TextView completedtxt3;

    @BindView(R.id.tickimg1)
    ImageView tickimg1;

    @BindView(R.id.tickimg2)
    ImageView tickimg2;

    @BindView(R.id.tickimg3)
    ImageView tickimg3;

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

        View rootView = inflater.inflate(R.layout.fragment_kyc, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        blv1.setEnabled(false);
        blv2.setEnabled(false);
        blv3.setEnabled(false);
        showSwipeProgress();
        mKYCDescriptionPresenter.fetchCurrentLevel();

        return rootView;
    }

    @OnClick(R.id.blv1)
    public void onLevel1Clicked() {
        FrameLayout frameLayout = getView().findViewById(R.id.levelcontainer);
        frameLayout.removeAllViews();
        replaceFragmentUsingFragmentManager(KYCLevel1Fragment.newInstance(), true,
                R.id.levelcontainer);
    }

    @OnClick(R.id.blv2)
    public void onLevel2Clicked() {
        FrameLayout frameLayout = getView().findViewById(R.id.levelcontainer);
        frameLayout.removeAllViews();
        replaceFragmentUsingFragmentManager(KYCLevel1Fragment.newInstance(), true,
                R.id.levelcontainer);
        replaceFragmentUsingFragmentManager(KYCLevel2Fragment.newInstance(), true,
                R.id.levelcontainer);
    }

    @OnClick(R.id.blv3)
    public void onLevel3Clicked() {
        FrameLayout frameLayout = getView().findViewById(R.id.levelcontainer);
        frameLayout.removeAllViews();
        replaceFragmentUsingFragmentManager(KYCLevel1Fragment.newInstance(), true,
                R.id.levelcontainer);
        replaceFragmentUsingFragmentManager(KYCLevel3Fragment.newInstance(), true,
                R.id.levelcontainer);
    }


    @Override
    public void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details) {
        hideSwipeProgress();
        int currentLevel = Integer.parseInt(kycLevel1Details.getCurrentLevel());

        if (currentLevel >= 0) {

            blv1.setEnabled(true);
        }
        if (currentLevel >= 1) {
            blv1.setEnabled(false);
            blv2.setEnabled(true);
            tickimg1.setImageResource(R.drawable.ic_tick);
            completedtxt1.setText(R.string.completion);
            blv1.setTextColor(getResources().getColor(R.color.colorAccent));
            blv1.getBackground().setColorFilter(getResources()
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY);
        }
        if (currentLevel >= 2) {
            blv2.setEnabled(false);
            blv3.setEnabled(true);
            tickimg2.setImageResource(R.drawable.ic_tick);
            completedtxt2.setText(R.string.completion);
            blv2.setTextColor(getResources().getColor(R.color.colorAccent));
            blv2.getBackground().setColorFilter(getResources()
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY);
        }
        if (currentLevel >= 3) {
            blv3.setEnabled(false);
            tickimg3.setImageResource(R.drawable.ic_tick);
            completedtxt3.setText(R.string.completion);
            blv3.setTextColor(getResources().getColor(R.color.colorAccent));
            blv3.getBackground().setColorFilter(getResources()
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void gotoHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }
}
