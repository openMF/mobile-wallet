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
 * Created by ankur on 18/June/2018
 */

public class SpecificTransactionsAdapter
        extends RecyclerView.Adapter<SpecificTransactionsAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactions;

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
        Transaction transaction = transactions.get(position);

        holder.mTvTransactionId.setText(
                Constants.TRANSACTION_ID + ": " + transaction.getTransactionId());
        holder.mTvTransactionDate.setText(Constants.DATE + ": " + transaction.getDate());
        holder.mTvTransactionAmount.setText(Utils.getFormattedAccountBalance(
                transaction.getAmount(), transaction.getCurrency().getCode()));

        holder.mTvFromClientName.setText(
                transaction.getTransferDetail().getFromClient().getDisplayName());
        holder.mTvFromAccountNo.setText(
                transaction.getTransferDetail().getFromAccount().getAccountNo());
        holder.mTvToClientName.setText(
                transaction.getTransferDetail().getToClient().getDisplayName());
        holder.mTvToAccountNo.setText(
                transaction.getTransferDetail().getToAccount().getAccountNo());

        switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.mTvTransactionStatus.setText(Constants.DEBIT);
                holder.mTvTransactionAmount.setTextColor(Color.RED);
                break;
            case CREDIT:
                holder.mTvTransactionStatus.setText(Constants.CREDIT);
                holder.mTvTransactionAmount.setTextColor(Color.parseColor("#009688"));
                break;
            case OTHER:
                holder.mTvTransactionStatus.setText(Constants.OTHER);
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

