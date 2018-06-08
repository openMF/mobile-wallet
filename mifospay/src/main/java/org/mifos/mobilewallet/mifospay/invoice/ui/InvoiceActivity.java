package org.mifos.mobilewallet.mifospay.invoice.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicePresenter;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceActivity extends BaseActivity implements InvoiceContract.InvoiceView {

    @Inject
    InvoicePresenter mPresenter;

    InvoiceContract.InvoicePresenter mInvoicePresenter;

    @BindView(R.id.tv_merchantId)
    TextView mTvMerchantId;

    @BindView(R.id.tv_consumerId)
    TextView mTvConsumerId;

    @BindView(R.id.tv_amount)
    TextView mTvAmount;

    @BindView(R.id.tv_itemsBought)
    TextView mTvItemsBought;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    @BindView(R.id.tv_transaction_id)
    TextView mTvTransactionId;

    @BindView(R.id.tv_paymentLink)
    TextView mTvPaymentLink;

    @BindView(R.id.tv_receiptLink)
    TextView mTvReceiptLink;

    @BindView(R.id.v_url)
    View mVUrl;

    @BindView(R.id.ll_url)
    LinearLayout mLlUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        setToolbarTitle("Invoice");
        showBackButton();
        mPresenter.attachView(this);

        Uri data = getIntent().getData();
        if (data != null) {
            showProgressDialog("Please wait..");
            mInvoicePresenter.getInvoiceDetails(data);
        } else {
            finish();
        }

    }

    @Override
    public void setPresenter(InvoiceContract.InvoicePresenter presenter) {
        mInvoicePresenter = presenter;
    }

    @Override
    public void showInvoiceDetails(final Invoice invoice, String merchantId, String paymentLink) {
        hideProgressDialog();

        mTvMerchantId.setText("Merchant: " + merchantId);
        mTvConsumerId.setText(
                "Consumer: " + invoice.getConsumerName() + " " + invoice.getConsumerId());
        mTvAmount.setText("Amount: INR " + invoice.getAmount() + "");
        mTvItemsBought.setText("Item(s): " + invoice.getItemsBought());
        String status = "Pending";
        if (invoice.getStatus() == 1) {
            status = "Done";

            mTvTransactionId.setVisibility(View.VISIBLE);
            mVUrl.setVisibility(View.VISIBLE);
            mLlUrl.setVisibility(View.VISIBLE);

            mTvTransactionId.setText("Transaction Id: " + invoice.getTransactionId());

            mTvReceiptLink.setText("https://receipt.mifospay.com/" + invoice.getTransactionId());
            mTvReceiptLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Unique Receipt Link",
                            mTvReceiptLink.getText().toString());
                    cm.setPrimaryClip(clipData);
                    showSnackbar("Unique Receipt Link copied to clipboard");
                    return true;
                }
            });
            mTvReceiptLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InvoiceActivity.this, ReceiptActivity.class);
                    intent.setData(Uri.parse(
                            "https://receipt.mifospay.com/" + invoice.getTransactionId()));
                    startActivity(intent);
                }
            });
        }

        mTvStatus.setText("Status: " + status);

        mTvPaymentLink.setText(paymentLink);
        mTvPaymentLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Unique Payment Link",
                        mTvPaymentLink.getText().toString());
                cm.setPrimaryClip(clipData);
                showSnackbar("Unique Payment Link copied to clipboard");
                return true;
            }
        });
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(findViewById(android.R.id.content), message);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        finish();
    }
}
