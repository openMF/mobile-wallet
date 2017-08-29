package org.mifos.mobilewallet.user.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.user.UserContract;
import org.mifos.mobilewallet.user.presenter.UserDetailsPresenter;
import org.mifos.mobilewallet.utils.TextDrawable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Client;

/**
 * Created by naman on 22/6/17.
 */

public class UserDetailsActivity extends BaseActivity implements UserContract.UserDetailsView {

    @Inject
    UserDetailsPresenter mPresenter;

    UserContract.UserDetailsPresenter mUserDetailsPresenter;

    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.tv_user_name)
    TextView ivUserName;

    @BindView(R.id.tv_user_details_name)
    TextView tvUserDetailsName;

    @BindView(R.id.tv_user_details_email)
    TextView tvUserDetailsEmail;

    @BindView(R.id.btn_add_pan)
    Button btnAddPan;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        setToolbarTitle("");
        showBackButton();

        mUserDetailsPresenter.getUserDetails();

        btnAddPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyPanDialog panDialog = new VerifyPanDialog();
                panDialog.show(getSupportFragmentManager(), "Pan dialog");
            }
        });

    }

    @Override
    public void setPresenter(UserContract.UserDetailsPresenter presenter) {
        mUserDetailsPresenter = presenter;
    }

    @Override
    public void showUserDetails(Client client) {
        ivUserName.setText(client.getName());
        tvUserDetailsName.setText(client.getName());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(client.getName().substring(0, 1), R.color.purple_200);
        ivUserImage.setImageDrawable(drawable);
    }

    @Override
    public void showPanStatus(boolean status) {
        if (pDialog != null) {
            pDialog.hide();
        }
        Snackbar snackbar = Snackbar
                .make(rootLayout, "", Snackbar.LENGTH_SHORT);

        if (status) {
            snackbar.setText("PAN number successfully verified");
        } else {
            snackbar.setText("Invalid PAN number");
        }

        snackbar.show();

    }

    public void verifyPan(String number) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
        }
        pDialog.setMessage("Verifying PAN...");
        pDialog.show();

        mUserDetailsPresenter.verifyPan(number);
    }
}
