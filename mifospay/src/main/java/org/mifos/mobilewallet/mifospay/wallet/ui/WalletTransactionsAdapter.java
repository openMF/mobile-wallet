package org.mifos.mobilewallet.mifospay.wallet.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mifos.org.mobilewallet.core.domain.model.Transaction;

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
                R.layout.item_wallet, parent, false);
        return new WalletTransactionsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WalletTransactionsAdapter.ViewHolder holder, int position) {

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

        @BindView(R.id.tv_account_name)
        TextView tvAccountName;

        @BindView(R.id.tv_account_balance)
        TextView tvAccountBalance;

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

