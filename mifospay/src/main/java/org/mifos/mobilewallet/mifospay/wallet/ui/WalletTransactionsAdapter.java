package org.mifos.mobilewallet.mifospay.wallet.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Transaction;

/**
 * Created by naman on 17/8/17.
 */

public class WalletTransactionsAdapter extends RecyclerView.Adapter<WalletTransactionsAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactions;

    @Inject
    public WalletTransactionsAdapter() {
    }

    @Override
    public WalletTransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_wallet_transaction, parent, false);
        return new WalletTransactionsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WalletTransactionsAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvTransactionAmount.setText(transaction.getCurrency().getCode() + " " +
                transaction.getAmount());
        holder.tvTransactionDate.setText(transaction.getDate());
        holder.tvTransactionId.setText(transaction.getTransactionId());

        switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.tvTransactionStatus.setText("Debit");
                holder.tvTransactionAmount.setTextColor(Color.RED);
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText("Credit");
                holder.tvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                break;
            case OTHER:
                holder.tvTransactionStatus.setText("Other");
                holder.tvTransactionAmount.setTextColor(Color.YELLOW);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_transaction_id)
        TextView tvTransactionId;

        @BindView(R.id.tv_transaction_status)
        TextView tvTransactionStatus;

        @BindView(R.id.tv_transaction_amount)
        TextView tvTransactionAmount;

        @BindView(R.id.tv_transaction_date)
        TextView tvTransactionDate;

        @BindView(R.id.iv_transaction_status)
        ImageView ivTransactionStatus;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setData(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public Transaction getTransaction(int position) {
        return transactions.get(position);
    }

}

