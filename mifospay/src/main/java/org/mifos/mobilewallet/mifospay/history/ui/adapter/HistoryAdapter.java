package org.mifos.mobilewallet.mifospay.history.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance;

/**
 * Created by naman on 17/8/17.
 */

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private Context context;

    @Inject
    public HistoryAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_casual_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        Double balance = transaction.getAmount();
        String currencyCode = transaction.getCurrency().getCode();
        holder.tvTransactionAmount
                .setText(getFormattedAccountBalance(balance, currencyCode));
        holder.tvTransactionDate.setText(transaction.getDate());

        if (balance > 0 && context != null) {
            int color = ContextCompat.getColor(context, R.color.colorAccentBlue);
            holder.tvTransactionAmount.setTextColor(color);
        }

        switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.tvTransactionStatus.setText(Constants.DEBIT);
                holder.tvTransactionStatus.setTextColor(ContextCompat.getColor(
                        context, R.color.colorDebit));
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText(Constants.CREDIT);
                holder.tvTransactionStatus.setTextColor(ContextCompat.getColor(
                        context, R.color.colorCredit));
                break;
            case OTHER:
                holder.tvTransactionStatus.setText(Constants.OTHER);
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

        @BindView(R.id.tv_item_casual_list_title)
        TextView tvTransactionAmount;

        @BindView(R.id.tv_item_casual_list_optional_caption)
        TextView tvTransactionStatus;

        @BindView(R.id.tv_item_casual_list_subtitle)
        TextView tvTransactionDate;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}

