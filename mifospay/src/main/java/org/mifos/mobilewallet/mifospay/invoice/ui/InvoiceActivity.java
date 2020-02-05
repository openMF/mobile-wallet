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

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.utils.DateHelper;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicePresenter;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
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

    @BindView(R.id.tv_date)
    TextView mTvDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        setToolbarTitle(Constants.INVOICE);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        mPresenter.attachView(this);

        Uri data = getIntent().getData();
        if (data != null) {
            showProgressDialog(Constants.PLEASE_WAIT);
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

        mTvMerchantId.setText(Constants.MERCHANT + ": " + merchantId);
        mTvConsumerId.setText(
                Constants.CONSUMER + ": " + invoice.getConsumerName() + " "
                        + invoice.getConsumerId());
        mTvAmount.setText(Constants.AMOUNT + ": " + Constants.INR + " " + invoice.getAmount() + "");
        mTvItemsBought.setText(Constants.ITEMS + ": " + invoice.getItemsBought());
        String status = Constants.PENDING;
        if (invoice.getStatus() == 1) {
            status = Constants.DONE;

            mTvTransactionId.setVisibility(View.VISIBLE);
            mVUrl.setVisibility(View.VISIBLE);
            mLlUrl.setVisibility(View.VISIBLE);

            mTvTransactionId.setText(Constants.TRANSACTION_ID + ": " + invoice.getTransactionId());

            mTvReceiptLink.setText(Constants.RECEIPT_DOMAIN + invoice.getTransactionId());
            mTvReceiptLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(Constants.UNIQUE_RECEIPT_LINK,
                            mTvReceiptLink.getText().toString());
                    cm.setPrimaryClip(clipData);
                    showSnackbar(Constants.UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD);
                    return true;
                }
            });
            mTvReceiptLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InvoiceActivity.this, ReceiptActivity.class);
                    intent.setData(Uri.parse(
                            Constants.RECEIPT_DOMAIN + invoice.getTransactionId()));
                    startActivity(intent);
                }
            });
        }

        mTvStatus.setText(Constants.STATUS + ": " + status);

        mTvPaymentLink.setText(paymentLink);
        mTvPaymentLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(Constants.UNIQUE_PAYMENT_LINK,
                        mTvPaymentLink.getText().toString());
                cm.setPrimaryClip(clipData);
                showSnackbar(Constants.UNIQUE_PAYMENT_LINK_COPIED_TO_CLIPBOARD);
                return true;
            }
        });

        mTvDate.setText(DateHelper.getDateAsString(invoice.getDate()));

        hideProgressDialog();
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
        dismissProgressDialog();
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(findViewById(android.R.id.content), message);
    }
    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
