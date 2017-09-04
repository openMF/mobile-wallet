package org.mifos.mobilewallet.auth.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 27/6/17.
 */

public class SmsOtpDialog extends BottomSheetDialogFragment {

    @BindView(R.id.btn_verify)
    Button btnVerify;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.et_sms_otp)
    EditText etSmsOtp;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_auto_detect_otp)
    TextView tvAutoDetect;

    private BottomSheetBehavior mBehavior;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_sms_otp, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp(etSmsOtp.getText().toString());
            }
        });

        autoDetectOtp();

        return dialog;
    }

    private void verifyOtp(String otp) {
        etSmsOtp.setText(otp);
        tvAutoDetect.setText("Verifying OTP");
        btnVerify.setText("Verifying..");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
                if (getActivity() instanceof SignupActivity) {
                    ((SignupActivity) getActivity()).openAddDetails();
                }
            }
        },  1500);

    }

    private void autoDetectOtp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyOtp("12345");
            }
        }, 2000);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}

