package org.mifos.mobilewallet.mifospay.registration.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 26/June/2018
 */

public class SignupMethod extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBottomSheetBehavior;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_choose_signup_method, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.btn_google_account)
    public void onGoogleAccountClicked() {
        dismiss();
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).signupUsingGoogleAccount();
        }
    }

    @OnClick(R.id.btn_no)
    public void onNoClicked() {
        dismiss();
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).signup();
        }
    }

}
