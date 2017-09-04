package org.mifos.mobilewallet.invoice.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 11/7/17.
 */

public class ExternalPaymentFragment extends Fragment {

    @BindView(R.id.btn_generate_qr)
    Button btnGenerateQr;

    @BindView(R.id.btn_send_sms)
    Button btnSendSms;

    View rootView;

    public static ExternalPaymentFragment newInstance() {
        ExternalPaymentFragment fragment = new ExternalPaymentFragment();
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
        rootView = inflater.inflate(R.layout.fragment_payment_external, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.btn_generate_qr)
    public void generateQRClicked() {
        if (getParentFragment() instanceof InvoiceFragment) {
            InvoiceFragment invoiceFragment = (InvoiceFragment) getParentFragment();
            invoiceFragment.createInvoice(new InvoiceFragment.InvoiceCallback() {
                @Override
                public void invoiceCreated(Invoice invoice) {
                    Intent intent = new Intent(getActivity(), ShowQrActivity.class);
                    intent.putExtra(Constants.QR_DATA, createQRStringData(invoice));
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.btn_send_sms)
    public void sendSmsClicked() {

    }

    private String createQRStringData(Invoice invoice) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("139.59.14.31")
                .appendPath("invoice")
                .appendQueryParameter("invoiceid", invoice.getInvoiceId())
                .appendQueryParameter("merchantid", String.valueOf(invoice.getMerchantId()))
                .appendQueryParameter("amount", String.valueOf(Math.round(invoice.getAmount())))
                .appendQueryParameter("accountid", String.valueOf(invoice.getAccountId()));
        return builder.build().toString();
    }
}
