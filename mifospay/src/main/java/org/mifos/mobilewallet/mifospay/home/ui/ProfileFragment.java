package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.editprofile.ui.EditProfileActivity;
import org.mifos.mobilewallet.mifospay.faq.ui.FAQActivity;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.ProfilePresenter;
import org.mifos.mobilewallet.mifospay.settings.ui.SettingsActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

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
        setToolbarTitle(Constants.PROFILE);
        hideBackButton();
        setSwipeEnabled(false);

        setHasOptionsMenu(true);

        mProfilePresenter.fetchprofile();
        mProfilePresenter.fetchClientImage();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_faq:
                startActivity(new Intent(getActivity(), FAQActivity.class));
                break;
            case R.id.item_edit_profile:
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;
            case R.id.item_profile_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void showProfile(Client client) {
        ivUserName.setText(client.getName());
        tvUserDetailsName.setText(client.getName());
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .width((int)getResources().getDimension(R.dimen.user_profile_image_size))
                .height((int)getResources().getDimension(R.dimen.user_profile_image_size))
                .endConfig().buildRound(client.getName().substring(0, 1), R.color.colorPrimary);
        ivUserImage.setImageDrawable(drawable);
        tvClientVpa.setText(client.getExternalId());
    }

    @Override
    public void fetchImageSuccess(ResponseBody responseBody) {

    }

    @Override
    public void setPresenter(HomeContract.ProfilePresenter presenter) {
        this.mProfilePresenter = presenter;
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
