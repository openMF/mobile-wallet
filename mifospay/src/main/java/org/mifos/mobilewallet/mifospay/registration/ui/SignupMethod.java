package org.mifos.mobilewallet.mifospay.registration.ui;

import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.mifos.mobilewallet.core.utils.Constants;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 26/June/2018
 */

public class SignupMethod extends BottomSheetDialogFragment {

    @BindView(R.id.cb_google_account)
    CheckBox mCbGoogleAccount;
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

    @OnClick(R.id.btn_merchant)
    public void onMerchantClicked() {
        dismiss();
        if (getActivity() instanceof LoginActivity) {
            if (mCbGoogleAccount.isChecked()) {
                ((LoginActivity) getActivity()).signupUsingGoogleAccount(
                        Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID);
            } else {
                ((LoginActivity) getActivity()).signup(Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID);
            }
        }
    }

    @OnClick(R.id.btn_customer)
    public void onCustomerClicked() {
        dismiss();
        if (getActivity() instanceof LoginActivity) {
            if (mCbGoogleAccount.isChecked()) {
                ((LoginActivity) getActivity()).signupUsingGoogleAccount(
                        Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID);
            } else {
                ((LoginActivity) getActivity()).signup(Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID);
            }
        }
    }

}
