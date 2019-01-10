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
 * KYCDescriptionFragment extends base Fragment as well as implements
 * four functions of KYCDescriptionView-Onfetchlevelsuccess,ShowToast,
 * goToHome,hideprogressDialog
 * @author ankur
 * @since  18/may/2018
 */

public class KYCDescriptionFragment extends
        BaseFragment implements KYCContract.KYCDescriptionView {


    /**
     * @Inject function is used import the dependencies
     * Here we are importing the KYCDescription presenter
     * and KYCContract.KYCDescriptionPresenter
      */

    @Inject
    //Importing the KYCDescriptionPresenter
    KYCDescriptionPresenter mPresenter;
    //Importing KYCContract.KYCDescriptionPresenter
    KYCContract.KYCDescriptionPresenter mKYCDescriptionPresenter;

    /**
     * To make the views like  cardview ,linear layout for all the three levels
     * in the kyc description fragment available in the the activity we use  the bindview
     * function of the butterknife library  and later use Butteknife.bind function
     * in on create  to finally bind the views
     */

    @BindView(R.id.cv_lvl1)     //Binding the card view of level1
    CardView cvLvl1;

    @BindView(R.id.cv_lvl2)     //Binding the card view of level2
    CardView cvLvl2;

    @BindView(R.id.cv_lvl3)     //Binding the card view of level3
    CardView cvLvl3;

    @BindView(R.id.ll_lvl1)     //Binding the linear layout of level1
    LinearLayout llLvl1;

    @BindView(R.id.ll_lvl2)     //Binding the linear layout of level2
    LinearLayout llLvl2;

    @BindView(R.id.ll_lvl3)     //Binding the linear layout of level3
    LinearLayout llLvl3;

    /**
     * Function to instantiate KYCDescriptionFragment
     * and using bundle to transfer data from one fragment to another
     */

    public static KYCDescriptionFragment newInstance() {

        Bundle args = new Bundle(); //instantiating bundle

        KYCDescriptionFragment fragment = new KYCDescriptionFragment(); //instantiating fragment
        fragment.setArguments(args);  //Attaching the bundle to the fragment for data transfering
        return fragment;
    }


    //Setter function for KYCDescriptionPresenter

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

        //inflating the kycdescription layout in this activity
        View rootView = inflater.inflate(R.layout.fragment_kyc_desc, container, false);
        //For binding the views in the activity
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        //Setting the toolbar title to COMPLETE KYC text
        setToolbarTitle(Constants.COMPLETE_KYC);
        //Showing the back button at the toolbar
        showBackButton();
        setSwipeEnabled(false);
        //Showing a progress dialog with a message of please wait
        showProgressDialog(Constants.PLEASE_WAIT);
        //Calling the fetchCurrentLevel to fetch the level of kyc the user is currently at
        mKYCDescriptionPresenter.fetchCurrentLevel();

        return rootView;
    }

    //On clicking cardview level1 we open the KYCLevel1Fragment in this activity

    @OnClick(R.id.cv_lvl1)
    public void onLevel1Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel1Fragment.newInstance(), true,
                R.id.container);
    }

    //On clicking cardview level2 we open the KYCLevel2Fragment in this activity

    @OnClick(R.id.cv_lvl2)
    public void onLevel2Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel2Fragment.newInstance(), true,
                R.id.container);
    }

    //On clicking cardview level3 we open the KYCLevel3Fragment in this activity

    @OnClick(R.id.cv_lvl3)
    public void onLevel3Clicked() {
        replaceFragmentUsingFragmentManager(KYCLevel3Fragment.newInstance(), true,
                R.id.container);
    }

    // Fetching the levels of KYC completed at the time of opening this fragment

    @Override
    public void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details) {

        int currentLevel = Integer.parseInt(kycLevel1Details.getCurrentLevel());

        /**
         *  After completing level1 we will set the cardview non clickable
         * and change the background colour of cardview
         * to a different colour, here it is green cardview
         */

        if (currentLevel >= 1) {
            cvLvl1.setClickable(false);
            llLvl1.setBackgroundResource(R.drawable.cardview_round_green);
        }

        /** After completing level2 we will set the cardview non
         * clickable and change the background colour of cardview
         * to a different colour, here it is green cardview
          */

        if (currentLevel >= 2) {
            cvLvl2.setClickable(false);
            llLvl2.setBackgroundResource(R.drawable.cardview_round_green);
        }

        /**
         * After completing level3 we will set the cardview not clickable and
         * change the background colour cardview  to a different colour
         */

        if (currentLevel >= 3) {
            cvLvl3.setClickable(false);
            llLvl3.setBackgroundResource(R.drawable.cardview_round_green);
        }

    }

    //Shows a toast for error message

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    //Opens the homeactivity intent at the time of connectivity error

    @Override
    public void gotoHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Function for hiding the progress bar
    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }
}
