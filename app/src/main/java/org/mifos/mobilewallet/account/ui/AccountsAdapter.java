package org.mifos.mobilewallet.account.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 11/7/17.
 */

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    private Context context;
    private List<Account> accounts;

    @Inject
    public AccountsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivAccountImage.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_account_balance_wallet));
        holder.tvAccountNumber.setText(accounts.get(position).getNumber());
        holder.tvAccountName.setText(accounts.get(position).getName());

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

        @BindView(R.id.iv_account_image)
        ImageView ivAccountImage;

        @BindView(R.id.tv_account_number)
        TextView tvAccountNumber;

        @BindView(R.id.tv_account_name)
        TextView tvAccountName;

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