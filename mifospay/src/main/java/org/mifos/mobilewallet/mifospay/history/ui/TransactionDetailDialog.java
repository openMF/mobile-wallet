package org.mifos.mobilewallet.mifospay.history.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.Account;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.presenter.TransactionDetailPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Utils;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.mifos.mobilewallet.mifospay.utils.Constants.CREDIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.DEBIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.OTHER;

/**
 * Created by ankur on 06/June/2018
 */

public class TransactionDetailDialog extends BottomSheetDialogFragment implements
        HistoryContract.TransactionDetailView {

    @Inject
    TransactionDetailPresenter mPresenter;
    HistoryContract.TransactionDetailPresenter mTransactionDetailPresenter;

    @Inject
    PreferencesHelper preferencesHelper;

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
    @BindView(R.id.pb_transaction_detail)
    ProgressBar progressBar;
    @BindView(R.id.ll_main)
    LinearLayout mLlMain;

    private BottomSheetBehavior mBottomSheetBehavior;
    private JournalEntry transaction;
    private ArrayList<JournalEntry> transactions;
    private String transactionType;

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
            transaction = bundle.getParcelable(Constants.TRANSACTION);
            transactions = bundle.getParcelableArrayList(Constants.TRANSACTIONS);
            if (transaction == null) {
                return dialog;
            }
        }
        mPresenter.attachView(this);
        showDetails(transaction);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void setPresenter(HistoryContract.TransactionDetailPresenter presenter) {
        mTransactionDetailPresenter = presenter;
    }

    @OnClick(R.id.tv_viewReceipt)
    public void viewReceipt() {
//        Intent intent = new Intent(getActivity(), ReceiptActivity.class);
//        intent.setData(Uri.parse(Constants.RECEIPT_DOMAIN + transaction.getTransactionId()));
//        startActivity(intent);
    }

    @OnClick(R.id.rl_from_to)
    public void showSpecificTransactions() {

        Intent intent = new Intent(getActivity(), SpecificTransactionsActivity.class);
        intent.putParcelableArrayListExtra(Constants.TRANSACTIONS, transactions);
        if (transactionType == DEBIT) {
            intent.putExtra(Constants.ACCOUNT_NUMBER, tvFromAccountNo.getText().toString());
            intent.putExtra(Constants.OTHER_CUSTOMER_NAME, tvFromClientName.getText().toString());
        } else if (transactionType == CREDIT) {
            intent.putExtra(Constants.ACCOUNT_NUMBER, tvToAccountNo.getText().toString());
            intent.putExtra(Constants.OTHER_CUSTOMER_NAME, tvToClientName.getText().toString());
        }
        startActivity(intent);

    }

    private void showDetails(JournalEntry transaction) {

        try {
            tvTransactionId.setText(Constants.TRANSACTION_ID + ": " +
                    transaction.getTransactionIdentifier());
            tvTransactionDate.setText(Constants.DATE + ": " + transaction.getTransactionDate());
            List<Account> creditors = transaction.getCreditors();
            List<Account> debtors = transaction.getDebtors();
            String creditAccountIdentifier = creditors.get(0).getAccountNumber();
            String debitAccountIdentifier = debtors.get(0).getAccountNumber();

            tvFromAccountNo.setText(creditAccountIdentifier);
            tvToAccountNo.setText(debitAccountIdentifier);

            Double amount = Double.valueOf(creditors.get(0).getAmount());
            transactionType = OTHER;
            if (preferencesHelper.getCustomerDepositAccountIdentifier()
                    .equals(creditAccountIdentifier)) {
                transactionType = CREDIT;
                /**
                 * Fetching the details of the debtor
                 */
                mTransactionDetailPresenter.fetchAccountDetail(debitAccountIdentifier);
                tvFromClientName.setText(preferencesHelper.getCustomerName());
            } else if (preferencesHelper.getCustomerDepositAccountIdentifier()
                    .equals(debitAccountIdentifier)) {
                transactionType = DEBIT;
                /**
                 * Fetching the details of the creditor
                 */
                mTransactionDetailPresenter.fetchAccountDetail(creditAccountIdentifier);
                tvToClientName.setText(preferencesHelper.getCustomerName());
            }

            tvTransactionAmount.setText(Utils.getFormattedAccountBalance(
                    amount, preferencesHelper.getCurrencySign()));

//        if (transaction.getTransactionType() != null) {
//            tvReceiptId.setVisibility(View.VISIBLE);
//            tvReceiptId.setText(Constants.RECEIPT_ID + ": " + transaction.getReceiptId());
//        }

            switch (transactionType) {
                case DEBIT:
                    tvTransactionStatus.setText(DEBIT);
                    tvTransactionAmount.setTextColor(Color.RED);
                    break;
                case CREDIT:
                    tvTransactionStatus.setText(CREDIT);
                    tvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                    break;
                case OTHER:
                    tvTransactionStatus.setText(OTHER);
                    tvTransactionAmount.setTextColor(Color.YELLOW);
                    break;
            }
        } catch (NullPointerException e) {
            showToast(getString(R.string.unexpected_error_subtitle));
            dismiss();
        }
    }


    @Override
    public void showCustomerName(String customerName) {
        rlFromTo.setVisibility(View.VISIBLE);
        vRule2.setVisibility(View.VISIBLE);
        if (transactionType.equals(DEBIT)) {
            tvFromClientName.setText(customerName);
        } else if (transactionType.equals(CREDIT)) {
            tvToClientName.setText(customerName);
        }
    }

    private void showToast(String message) {
        Toaster.showToast(getActivity(), message);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        showToast(message);
        dismiss();
    }

}
