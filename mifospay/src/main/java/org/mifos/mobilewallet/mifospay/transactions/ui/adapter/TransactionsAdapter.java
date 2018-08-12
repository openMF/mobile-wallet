package org.mifos.mobilewallet.mifospay.transactions.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/8/17.
 */

public class TransactionsAdapter
        extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactions;

    @Inject
    public TransactionsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvTransactionAmount.setText(transaction.getCurrency().getCode() + " " +
                transaction.getAmount());
        holder.tvTransactionDate.setText(transaction.getDate());
        holder.tvTransactionId.setText(transaction.getTransactionId());

        switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.tvTransactionStatus.setText(Constants.DEBIT);
                holder.tvTransactionAmount.setTextColor(Color.RED);
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText(Constants.CREDIT);
                holder.tvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                break;
            case OTHER:
                holder.tvTransactionStatus.setText(Constants.OTHER);
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

    public void setData(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public ArrayList<Transaction> getTransactions() {
        return (ArrayList<Transaction>) transactions;
    }

    public Transaction getTransaction(int position) {
        return transactions.get(position);
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

}

