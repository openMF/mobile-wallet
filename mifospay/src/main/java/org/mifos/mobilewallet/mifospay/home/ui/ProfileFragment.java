package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.editprofile.ui.EditProfileActivity;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.ProfilePresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

/**
 * Created by naman on 7/9/17.
 */

public class ProfileFragment extends BaseFragment implements BaseHomeContract.ProfileView {

    @Inject
    ProfilePresenter mPresenter;

    BaseHomeContract.ProfilePresenter mProfilePresenter;

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @BindView(R.id.nsv_profile_bottom_sheet_dialog)
    View vProfileBottomSheetDialog;

    @BindView(R.id.inc_account_details_email)
    View vAccountDetailsEmail;

    @BindView(R.id.inc_account_details_vpa)
    View vAccountDetailsVpa;

    @BindView(R.id.inc_account_details_mobile_number)
    View vAccountDetailsMobile;

    protected static BottomSheetBehavior mBottomSheetBehavior;

    public static ProfileFragment newInstance(long clientId) {

        Bundle args = new Bundle();
        args.putLong(Constants.CLIENT_ID, clientId);
        ProfileFragment fragment = new ProfileFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        setupUi();

        return rootView;
    }

    private void setupUi() {
        setToolbarTitle(Constants.PROFILE);
        setSwipeEnabled(false);
        hideBackButton();
        setupBottomSheet();
    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior
                .from(vProfileBottomSheetDialog);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mProfilePresenter.fetchProfile();
        mProfilePresenter.fetchAccountDetails();
        mProfilePresenter.fetchClientImage();
    }

    @Override
    public void setPresenter(BaseHomeContract.ProfilePresenter presenter) {
        this.mProfilePresenter = presenter;
    }

    @OnClick(R.id.iv_user_image)
    public void onUserImageEditClicked() {
        if (getActivity() != null) {
            Intent i = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(i);
        }
    }

    @OnClick(R.id.btn_profile_bottom_sheet_action)
    public void onEditProfileClicked() {
        if (getActivity() != null) {
            getActivity().startActivity(new Intent(getActivity(), EditProfileActivity.class));
        }
    }

    @Override
    public void showProfile(Client client) {
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .width((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .height((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .endConfig().buildRound(client.getName().substring(0, 1), R.color.colorAccentBlack);
        ivUserImage.setImageDrawable(drawable);
        tvUserName.setText(client.getName());
    }

    @Override
    public void showEmail(String email) {
        ((ImageView) vAccountDetailsEmail.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
        ((TextView) vAccountDetailsEmail.findViewById(R.id.tv_item_casual_list_title))
                .setText(email);
        ((TextView) vAccountDetailsEmail.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.email));
    }

    @Override
    public void showVpa(String vpa) {
        ((ImageView) vAccountDetailsVpa.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction));
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_title))
                .setText(vpa);
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.vpa));
    }

    @Override
    public void showMobile(String mobile) {
        ((ImageView) vAccountDetailsMobile.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_mobile));
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_title))
                .setText(mobile);
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.mobile));
    }

    @Override
    public void fetchImageSuccess(ResponseBody responseBody) {

    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getActivity(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }

}
