package org.mifos.mobilewallet.mifospay.transactions.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.transactions.presenter.TransactionDetailPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 06/June/2018
 */

public class TransactionDetailDialog extends BottomSheetDialogFragment implements
        TransactionsContract.TransactionDetailView {

    @Inject
    TransactionDetailPresenter mPresenter;

    TransactionsContract.TransactionDetailPresenter mTransactionDetailPresenter;

    @BindView(R.id.tv_transaction_id)
    TextView tvTransactionId;

    @BindView(R.id.tv_transaction_date)
    TextView tvTransactionDate;

    @BindView(R.id.tv_receiptId)
    TextView tvReceiptId;

    @BindView(R.id.tv_transaction_status)
    TextView tvTransactionStatus;

    @BindView(R.id.tv_transaction_amount)
    TextView tvTransactionAmount;

    @BindView(R.id.rl_from_to)
    RelativeLayout rlFromTo;

    @BindView(R.id.iv_fromImage)
    ImageView ivFromImage;

    @BindView(R.id.tv_fromClientName)
    TextView tvFromClientName;

    @BindView(R.id.tv_fromAccountNo)
    TextView tvFromAccountNo;

    @BindView(R.id.iv_toImage)
    ImageView ivToImage;

    @BindView(R.id.tv_toClientName)
    TextView tvToClientName;

    @BindView(R.id.tv_toAccountNo)
    TextView tvToAccountNo;

    @BindView(R.id.v_rule2)
    View vRule2;

    @BindView(R.id.tv_viewReceipt)
    TextView tvViewReceipt;
    @BindView(R.id.ll_from)
    LinearLayout mLlFrom;
    @BindView(R.id.ll_to)
    LinearLayout mLlTo;


    private BottomSheetBehavior mBottomSheetBehavior;
    private Transaction transaction;
    private String accountNo;

    @Override
    public void setPresenter(TransactionsContract.TransactionDetailPresenter presenter) {
        mTransactionDetailPresenter = presenter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_transaction_detail, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            transaction = bundle.getParcelable("transaction");
            accountNo = bundle.getString("accountNo");
            if (transaction == null) {
                return dialog;
            }
        }
        mPresenter.attachView(this);

        tvTransactionId.setText("Transaction ID: " + transaction.getTransactionId());
        tvTransactionDate.setText("Date: " + transaction.getDate());
        tvTransactionAmount.setText(
                transaction.getCurrency().getCode() + " " + transaction.getAmount());

        if (transaction.getTransferId() != 0) {
            rlFromTo.setVisibility(View.VISIBLE);
            vRule2.setVisibility(View.VISIBLE);

            tvFromClientName.setText(
                    transaction.getTransferDetail().getFromClient().getDisplayName());
            tvFromAccountNo.setText(
                    transaction.getTransferDetail().getFromAccount().getAccountNo());
            tvToClientName.setText(transaction.getTransferDetail().getToClient().getDisplayName());
            tvToAccountNo.setText(transaction.getTransferDetail().getToAccount().getAccountNo());
        }

        if (transaction.getReceiptId() != null) {
            tvReceiptId.setVisibility(View.VISIBLE);
            tvReceiptId.setText("Receipt ID: " + transaction.getReceiptId());
        }

        switch (transaction.getTransactionType()) {
            case DEBIT:
                tvTransactionStatus.setText("Debit");
                tvTransactionAmount.setTextColor(Color.RED);
                break;
            case CREDIT:
                tvTransactionStatus.setText("Credit");
                tvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                break;
            case OTHER:
                tvTransactionStatus.setText("Other");
                tvTransactionAmount.setTextColor(Color.YELLOW);
                break;
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.tv_viewReceipt)
    public void viewReceipt() {
        Intent intent = new Intent(getActivity(), ReceiptActivity.class);
        intent.setData(Uri.parse("https://receipt.mifospay.com/" + transaction.getTransactionId()));
        startActivity(intent);
    }

    @OnClick(R.id.ll_from)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), SpecificTransactionsActivity.class);
        if (!tvFromAccountNo.getText().toString().equals(accountNo)) {
            // this is second account
            TransferDetail transactionDetail = transaction.getTransferDetail();
            intent.putExtra("accountNo2", transactionDetail.getFromAccount());
            intent.putExtra("clientName2",
                    transactionDetail.getFromClient().getDisplayName());
            intent.putExtra("accountNo1", transactionDetail.getToAccount());
            intent.putExtra("clientName1",
                    transactionDetail.getToClient().getDisplayName());
//                    intent.putExtra("accountId", accountId);
            startActivity(intent);
        }
    }

}
