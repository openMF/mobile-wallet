package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @BindView(R.id.img1)
    ImageView img1;

    @BindView(R.id.img2)
    ImageView img2;

    @BindView(R.id.img3)
    ImageView img3;

    @BindView(R.id.first)
    LinearLayout f;

    @BindView(R.id.second)
    LinearLayout s;

    @BindView(R.id.third)
    LinearLayout t;

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

    @BindView(R.id.lvl1text)
    TextView text1;

    @BindView(R.id.lvl2text)
    TextView text2;

    @BindView(R.id.lvl3text)
    TextView text3;

    @BindView(R.id.completed1)
    TextView completed1;

    @BindView(R.id.completed2)
    TextView completed2;

    @BindView(R.id.completed3)
    TextView completed3;

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
        setSwipeEnabled(false);

        showProgressDialog(Constants.PLEASE_WAIT);
        mKYCDescriptionPresenter.fetchCurrentLevel();

        return rootView;
    }

    @OnClick(R.id.cv_lvl1)
    public void onLevel1Clicked() {
        KYCLevel1Fragment kycLevel1Fragment=new KYCLevel1Fragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.finalcontainer,kycLevel1Fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.cv_lvl2)
    public void onLevel2Clicked() {
        KYCLevel2Fragment kycLevel2Fragment=new KYCLevel2Fragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.finalcontainer,kycLevel2Fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.cv_lvl3)
    public void onLevel3Clicked() {

        KYCLevel3Fragment kycLevel3Fragment=new KYCLevel3Fragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.finalcontainer,kycLevel3Fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details) {

        int currentLevel = Integer.parseInt(kycLevel1Details.getCurrentLevel());

        if (currentLevel >= 1) {
            cvLvl1.setClickable(false);
            text1.setTextColor(getResources().getColor(R.color.colorofkyvlevelsbutton));
            completed1.setText("Completed");
            img1.setImageResource(R.drawable.ic_tick);
            llLvl1.setBackgroundResource(R.drawable.cardview_round_white);
        }
        if (currentLevel >= 2) {
            cvLvl2.setClickable(false);
            text2.setTextColor(getResources().getColor(R.color.colorofkyvlevelsbutton));
            completed2.setText("Completed");
            img2.setImageResource(R.drawable.ic_tick);
            llLvl2.setBackgroundResource(R.drawable.cardview_round_white);
        }
        if (currentLevel >= 3) {
            cvLvl3.setClickable(false);
            text3.setTextColor(getResources().getColor(R.color.colorofkyvlevelsbutton));
            completed3.setText("Completed");
            img3.setImageResource(R.drawable.ic_tick);
            llLvl3.setBackgroundResource(R.drawable.cardview_round_white);
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
