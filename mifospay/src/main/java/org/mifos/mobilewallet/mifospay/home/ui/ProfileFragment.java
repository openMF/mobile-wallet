package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Client;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.ProfilePresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 7/9/17.
 */

public class ProfileFragment extends BaseFragment implements HomeContract.ProfileView {

    @Inject
    ProfilePresenter mPresenter;

    HomeContract.ProfilePresenter mProfilePresenter;

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.tv_user_name)
    TextView ivUserName;

    @BindView(R.id.tv_user_details_name)
    TextView tvUserDetailsName;

    @BindView(R.id.tv_client_vpa)
    TextView tvClientVpa;


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
        setToolbarTitle("Profile");
        hideBackButton();
        setSwipeEnabled(false);

        mProfilePresenter.fetchprofile();

        return rootView;
    }

    @Override
    public void showProfile(Client client) {
        Log.d("qxz", "showProfile: " + client);

        ivUserName.setText(client.getName());
        tvUserDetailsName.setText(client.getName());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(client.getName().substring(0, 1), R.color.colorPrimary);
        ivUserImage.setImageDrawable(drawable);
        tvClientVpa.setText(client.getExternalId());
    }

    @Override
    public void setPresenter(HomeContract.ProfilePresenter presenter) {
        this.mProfilePresenter = presenter;
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }


}
