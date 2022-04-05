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
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.presenter.UpiPinPresenter;
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 13/July/2018
 */

public class UpiPinFragment extends BaseFragment implements BankContract.UpiPinView {

    @Inject
    UpiPinPresenter mPresenter;
    BankContract.UpiPinPresenter mUpiPinPresenter;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.pe_upi_pin)
    PinEntryEditText mPeUpiPin;

    private int step;
    private String upiPin;

    public static UpiPinFragment newInstance(int step, String upiPin) {

        Bundle args = new Bundle();

        args.putInt(Constants.STEP, step);
        args.putString(Constants.UPI_PIN, upiPin);

        UpiPinFragment fragment = new UpiPinFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_upi_pin_setup,
                container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        Bundle b = getArguments();
        if (b != null) {
            step = b.getInt(Constants.STEP, 0);
            upiPin = b.getString(Constants.UPI_PIN, null);
            mTvTitle.setText(R.string.reenter_upi);
        }

        mPeUpiPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    okayClicked();
                    return true;
                }
                return false;
            }
        });
        mPeUpiPin.requestFocus();
        return rootView;
    }

    public void okayClicked() {
        if (getActivity() instanceof SetupUpiPinActivity) {
            if (mPeUpiPin.getText().toString().length() == 4) {
                if (step == 1) {
                    if (upiPin.equals(mPeUpiPin.getText().toString())) {
                        ((SetupUpiPinActivity) getActivity()).upiPinConfirmed(upiPin);
                    } else {
                        showToast(getString(R.string.upi_pin_mismatch));
                    }
                } else {
                    ((SetupUpiPinActivity) getActivity()).upiPinEntered(
                            mPeUpiPin.getText().toString());
                }
            } else {
                showToast(getString(R.string.enter_upi_length_4));
            }
        }
    }

    public void showToast(String message) {
        Toaster.showToast(getActivity(), message);
    }

    @Override
    public void setPresenter(BankContract.UpiPinPresenter presenter) {
        mUpiPinPresenter = presenter;
    }
}
