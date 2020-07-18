package org.mifos.mobilewallet.mifospay.bank.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.presenter.DebitCardPresenter;
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

public class DebitCardFragment extends BaseFragment implements BankContract.DebitCardView {

    @Inject
    DebitCardPresenter mPresenter;
    BankContract.DebitCardPresenter mDebitCardPresenter;

    @BindView(R.id.et_debit_card_number)
    EditText mEtDebitCardNumber;
    @BindView(R.id.pe_month)
    PinEntryEditText mPeMonth;
    @BindView(R.id.pe_year)
    PinEntryEditText mPeYear;

    @Override
    public void setPresenter(BankContract.DebitCardPresenter presenter) {
        mDebitCardPresenter = presenter;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_debit_card,
                container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        mPeYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    okayClicked();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    public void okayClicked() {
        showProgressDialog(Constants.PLEASE_WAIT);

        mDebitCardPresenter.verifyDebitCard(mEtDebitCardNumber.getText()
                .toString(), mPeMonth.getText().toString(), mPeYear.getText().toString());

    }

    @Override
    public void verifyDebitCardSuccess(String otp) {
        hideProgressDialog();
        if (getActivity() instanceof SetupUpiPinActivity) {
            ((SetupUpiPinActivity) getActivity()).debitCardVerified(otp);
        }
    }

    @Override
    public void verifyDebitCardError(String message) {
        showToast(message);
    }

    public void showToast(String message) {
        Toaster.showToast(getActivity(), message);
    }
}
