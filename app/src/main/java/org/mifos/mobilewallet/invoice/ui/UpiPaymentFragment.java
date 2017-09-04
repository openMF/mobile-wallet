package org.mifos.mobilewallet.invoice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 27/6/17.
 */

public class UpiPaymentFragment extends Fragment {

    View rootView;

    @BindView(R.id.btn_request)
    Button btnRequest;

    @BindView(R.id.et_vpa)
    EditText etVpa;

    public static UpiPaymentFragment newInstance() {
        UpiPaymentFragment fragment = new UpiPaymentFragment();
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
        rootView = inflater.inflate(R.layout.fragment_payment_upi, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.btn_request)
    public void requestClicked() {
        int total = ((InvoiceFragment) getParentFragment()).getInvoiceAmount();
        String vpa = etVpa.getText().toString();

        Intent intent = new Intent(getActivity(), PaymentSuccessActivity.class);
        intent.putExtra(Constants.INVOICE_AMOUNT, total);
        intent.putExtra(Constants.INVOICE_PAYMENT_UPI_VPA, vpa);
        startActivity(intent);
    }

}
