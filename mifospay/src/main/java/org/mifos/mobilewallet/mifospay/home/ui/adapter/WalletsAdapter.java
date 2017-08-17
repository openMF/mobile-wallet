package org.mifos.mobilewallet.mifospay.home.ui.adapter;

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
import mifos.org.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 17/8/17.
 */

public class WalletsAdapter extends RecyclerView.Adapter<WalletsAdapter.ViewHolder> {

    private Context context;
    private List<Account> accounts;

    @Inject
    public WalletsAdapter() {
    }

    @Override
    public WalletsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_wallet, parent, false);
        return new WalletsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WalletsAdapter.ViewHolder holder, int position) {
        holder.tvAccountName.setText(accounts.get(position).getName());
        holder.tvAccountBalance.setText(accounts.get(position).getCurrency().getDisplaySymbol() +
                accounts.get(position).getBalance());

    }

    @Override
    public int getItemCount() {
        if (accounts != null) {
            return accounts.size();
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

    public void setData(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public Account getAccount(int position) {
        return accounts.get(position);
    }

}
