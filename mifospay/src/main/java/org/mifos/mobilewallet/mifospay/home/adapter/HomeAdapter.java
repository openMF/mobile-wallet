package org.mifos.mobilewallet.mifospay.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/8/17.
 */

public class HomeAdapter
        extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactions;

    @Inject
    public HomeAdapter() {
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

        String balance = Utils.getFormattedAccountBalance(transaction.getAmount());

        holder.tvTransactionAmount.setText(balance);
        holder.tvTransactionDate.setText(transaction.getDate());

        switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.tvTransactionStatus.setText(Constants.DEBIT);
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText(Constants.CREDIT);
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
        TextView tvTransactionStatus;

        @BindView(R.id.tv_item_casual_list_optional_caption)
        TextView tvTransactionAmount;

        @BindView(R.id.tv_item_casual_list_subtitle)
        TextView tvTransactionDate;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
