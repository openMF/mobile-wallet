package org.mifos.mobilewallet.mifospay.bank.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.AnimationUtil;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ankur on 16/July/2018
 */

public class UpiSetupHolderDialog extends BottomSheetDialogFragment {

    @BindView(R.id.et_debit_card_number)
    EditText mEtDebitCardNumber;
    @BindView(R.id.pe_month)
    PinEntryEditText mPeMonth;
    @BindView(R.id.pe_year)
    PinEntryEditText mPeYear;
    @BindView(R.id.ll_debit_card)
    LinearLayout mLlDebitCard;
    @BindView(R.id.tv_otp_title)
    TextView mTvOtpTitle;
    @BindView(R.id.pe_otp)
    PinEntryEditText mPeOtp;
    @BindView(R.id.ll_otp)
    LinearLayout mLlOtp;
    @BindView(R.id.tv_upi_title)
    TextView mTvUpiTitle;
    @BindView(R.id.pe_upi_pin)
    PinEntryEditText mPeUpiPin;
    @BindView(R.id.upi)
    LinearLayout mUpi;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_okay)
    Button mBtnOkay;
    Unbinder unbinder;

    private BottomSheetBehavior mBottomSheetBehavior;
    private String type;

    public static UpiSetupHolderDialog newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(Constants.TYPE, type);
        UpiSetupHolderDialog fragment = new UpiSetupHolderDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_upi_setup_holderr, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, view);

        Bundle b = getArguments();
        if (b != null) {
            type = b.getString(Constants.TYPE);
            if (type.equals(Constants.SETUP) || type.equals(Constants.FORGOT)) {
                AnimationUtil.expand(mLlDebitCard);
            } else {
                AnimationUtil.expand(mLlOtp);
            }
        }
        DebugUtil.log(b ,type);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
