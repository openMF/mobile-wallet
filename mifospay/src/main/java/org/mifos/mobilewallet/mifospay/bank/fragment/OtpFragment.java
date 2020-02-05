package org.mifos.mobilewallet.mifospay.bank.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 13/July/2018
 */

public class OtpFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.pe_otp)
    PinEntryEditText mPeOtp;

    private String otp;

    public static OtpFragment newInstance(String otp) {

        Bundle args = new Bundle();

        args.putString(Constants.OTP, otp);

        OtpFragment fragment = new OtpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);

        if (getArguments() != null) {
            otp = getArguments().getString(Constants.OTP);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_otp, container, false);
        ButterKnife.bind(this, rootView);

        mPeOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    okayClicked();
                    return true;
                }
                return false;
            }
        });
        mPeOtp.requestFocus();
        return rootView;
    }

    public void okayClicked() {
        if (getActivity() instanceof SetupUpiPinActivity) {
            if (mPeOtp.getText().toString().equals(otp)) {
                ((SetupUpiPinActivity) getActivity()).otpVerified();
            } else {
                showToast(getString(R.string.wrong_otp));
            }
        }
    }

    public void showToast(String message) {
        Toaster.showToast(getActivity(), message);
    }
}
