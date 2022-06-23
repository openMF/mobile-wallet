package org.mifos.mobilewallet.mifospay.bank.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.ListItemOnClick;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 09/July/2018
 */

public class BankAccountsAdapter extends RecyclerView.Adapter<BankAccountsAdapter.ViewHolder> {

    private final ListItemOnClick onClickListener;

    private List<BankAccountDetails> mBankAccountDetailsList;



    public BankAccountsAdapter(ListItemOnClick onClickListener) {
        this.onClickListener = onClickListener;
        mBankAccountDetailsList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_casual_list,
                parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(v1 -> onClickListener.onClick(vh.getBindingAdapterPosition()));
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BankAccountDetails bankAccountDetails = mBankAccountDetailsList.get(position);
        holder.mTvBankName.setText(bankAccountDetails.getBankName());
        holder.mTvAccountHolderName.setText(bankAccountDetails.getAccountholderName());
        holder.mTvBranch.setText(bankAccountDetails.getBranch());
        holder.imageViewAccount.setImageResource(R.drawable.ic_bank);
    }

    @Override
    public int getItemCount() {
        if (mBankAccountDetailsList != null) {
            return mBankAccountDetailsList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<BankAccountDetails> bankAccountDetailsList) {
        this.mBankAccountDetailsList = bankAccountDetailsList;
        notifyDataSetChanged();
    }

    public BankAccountDetails getBankDetails(int position) {
        return mBankAccountDetailsList.get(position);
    }

    public void addBank(BankAccountDetails bankAccountDetails) {
        mBankAccountDetailsList.add(bankAccountDetails);
        notifyDataSetChanged();
        DebugUtil.log(mBankAccountDetailsList.size());
    }

    public void setBankDetails(int index, BankAccountDetails bankAccountDetails) {
        mBankAccountDetailsList.set(index, bankAccountDetails);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_casual_list_title)
        TextView mTvBankName;
        @BindView(R.id.tv_item_casual_list_subtitle)
        TextView mTvAccountHolderName;
        @BindView(R.id.tv_item_casual_list_optional_caption)
        TextView mTvBranch;
        @BindView(R.id.iv_item_casual_list_icon)
        ImageView imageViewAccount;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}