package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 27/6/17.
 */

public class PaymentSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_payment_success)
    TextView tvPaymentSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_success);
        ButterKnife.bind(this);

        setToolbarTitle("Success");
        showBackButton();

        int total = getIntent().getIntExtra(Constants.INVOICE_AMOUNT, 0);
        String vpa = getIntent().getStringExtra(Constants.INVOICE_PAYMENT_UPI_VPA);

        tvPaymentSuccess.setText("You have succesfully received " + Constants.RUPEE
                + " " + total + " from " + vpa);
    }
}
