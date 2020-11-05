package org.mifos.mobilewallet.mifospay.history.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.Account;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mifos.mobilewallet.mifospay.utils.Constants.CREDIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.DEBIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.OTHER;

/**
 * Created by ankur on 18/June/2018
 */

public class SpecificTransactionsAdapter
        extends RecyclerView.Adapter<SpecificTransactionsAdapter.ViewHolder> {

    private Context context;
    private List<JournalEntry> transactions;
    private String currencySign;
    private String accountIdentifier;
    private String customerName;
    private String otherCustomerName;

    @Inject
    public SpecificTransactionsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_specific_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JournalEntry transaction = transactions.get(position);

        holder.mTvTransactionId.setText(
                Constants.TRANSACTION_ID + ": " + transaction.getTransactionIdentifier());

        String formattedTransactionDate = Utils.getFormattedDate(transaction.getTransactionDate());
        holder.mTvTransactionDate.setText(Constants.DATE + ": " + formattedTransactionDate);

        List<Account> creditors = transaction.getCreditors();
        List<Account> debtors = transaction.getDebtors();
        Double amount = Double.valueOf(creditors.get(0).getAmount());

        holder.mTvTransactionAmount.setText(Utils.getFormattedAccountBalance(amount, currencySign));

        String transactionType = DEBIT;
        if (accountIdentifier.equals(creditors.get(0).getAccountNumber())) {
            transactionType = CREDIT;
        }

        holder.mTvFromAccountNo.setText(creditors.get(0).getAccountNumber());
        holder.mTvToAccountNo.setText(debtors.get(0).getAccountNumber());

        switch (transactionType) {
            case DEBIT:
                holder.mTvTransactionStatus.setText(DEBIT);
                holder.mTvTransactionAmount.setTextColor(Color.RED);
                holder.mTvToClientName.setText(customerName);
                holder.mTvFromClientName.setText(otherCustomerName);
                break;
            case CREDIT:
                holder.mTvTransactionStatus.setText(CREDIT);
                holder.mTvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                holder.mTvToClientName.setText(otherCustomerName);
                holder.mTvFromClientName.setText(customerName);
                break;
            case OTHER:
                holder.mTvTransactionStatus.setText(OTHER);
                holder.mTvTransactionAmount.setTextColor(Color.YELLOW);
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (transactions != null) {
            return transactions.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<JournalEntry> transactions, String currencySign,
                        String accountIdentifier, String customerName,
                        String otherCustomerName) {
        this.transactions = transactions;
        this.currencySign  = currencySign;
        this.accountIdentifier = accountIdentifier;
        this.customerName = customerName;
        this.otherCustomerName = otherCustomerName;
        notifyDataSetChanged();
    }

    public ArrayList<JournalEntry> getTransactions() {
        return (ArrayList<JournalEntry>) transactions;
    }

    public JournalEntry getTransaction(int position) {
        return transactions.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_transaction_status)
        ImageView mIvTransactionStatus;
        @BindView(R.id.tv_transaction_id)
        TextView mTvTransactionId;
        @BindView(R.id.tv_transaction_date)
        TextView mTvTransactionDate;
        @BindView(R.id.tv_transaction_status)
        TextView mTvTransactionStatus;
        @BindView(R.id.tv_transaction_amount)
        TextView mTvTransactionAmount;
        @BindView(R.id.iv_fromImage)
        ImageView mIvFromImage;
        @BindView(R.id.tv_fromClientName)
        TextView mTvFromClientName;
        @BindView(R.id.tv_fromAccountNo)
        TextView mTvFromAccountNo;
        @BindView(R.id.ll_from)
        LinearLayout mLlFrom;
        @BindView(R.id.iv_toImage)
        ImageView mIvToImage;
        @BindView(R.id.tv_toClientName)
        TextView mTvToClientName;
        @BindView(R.id.tv_toAccountNo)
        TextView mTvToAccountNo;
        @BindView(R.id.ll_to)
        LinearLayout mLlTo;
        @BindView(R.id.rl_from_to)
        RelativeLayout mRlFromTo;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}

