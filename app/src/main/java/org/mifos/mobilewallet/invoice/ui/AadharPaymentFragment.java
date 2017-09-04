package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by naman on 27/6/17.
 */

public class AadharPaymentFragment extends Fragment {

    View rootView;

    @BindView(R.id.et_aadhar_number)
    EditText etAadhar;

    @BindView(R.id.btn_scan_fp)
    Button btnScanFp;

    private static final int AADHAR_NUMBER_TOTAL_SYMBOLS = 19;
    private static final int AADHAR_NUMBER_TOTAL_DIGITS = 16;
    private static final int AADHAR_NUMBER_DIVIDER_MODULO = 5;
    private static final int AADHAR_NUMBER_DIVIDER_POSITION = AADHAR_NUMBER_DIVIDER_MODULO - 1;
    private static final char AADHAR_NUMBER_DIVIDER = '-';

    public static AadharPaymentFragment newInstance() {
        AadharPaymentFragment fragment = new AadharPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_aadhar, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnTextChanged(value = R.id.et_aadhar_number, callback =
            OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, AADHAR_NUMBER_TOTAL_SYMBOLS, AADHAR_NUMBER_DIVIDER_MODULO,
                AADHAR_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, AADHAR_NUMBER_TOTAL_DIGITS),
                    AADHAR_NUMBER_DIVIDER_POSITION, AADHAR_NUMBER_DIVIDER));
        }
    }

    @OnClick(R.id.btn_scan_fp)
    public void scanFPClicked() {
        ScanFPDialog scanFPDialog = new ScanFPDialog();
        scanFPDialog.show(getChildFragmentManager(), "ScanFPDialog");
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

}

