package org.mifos.mobilewallet.mifospay.merchants.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class is the Adapter class for MerchantAdapter.
 * @author ankur
 * @since 11/July/2018
 */

public class MerchantsAdapter extends RecyclerView.Adapter<MerchantsAdapter.ViewHolder> implements
        Filterable {

    private List<SavingsWithAssociations> mMerchantsList;
    private List<SavingsWithAssociations> mMerchantsFilteredList;

    @Inject
    public MerchantsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SavingsWithAssociations mMerchant = mMerchantsList.get(position);
        holder.mTvMerchantName.setText(mMerchant.getClientName());
        holder.mTvMerchantExternalId.setText(mMerchant.getExternalId());
    }

    @Override
    public int getItemCount() {
        if (mMerchantsList != null) {
            return mMerchantsList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<SavingsWithAssociations> mMerchantsList) {
        this.mMerchantsList = mMerchantsList;
        notifyDataSetChanged();
    }

    public List<SavingsWithAssociations> getMerchantsList() {
        return mMerchantsList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                DebugUtil.log("adapter", charString);
                if (charString.isEmpty()) {
                    mMerchantsFilteredList = mMerchantsList;
                } else {
                    List<SavingsWithAssociations> filteredList = new ArrayList<>();
                    for (SavingsWithAssociations merchant : mMerchantsList) {
                        if (merchant.getClientName().toLowerCase().contains(
                                charString.toLowerCase())
                                || merchant.getExternalId().toLowerCase().contains(
                                charString.toLowerCase())) {
                            filteredList.add(merchant);
                        }
                    }

                    mMerchantsFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mMerchantsFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mMerchantsFilteredList = (List<SavingsWithAssociations>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filterList(List<SavingsWithAssociations> filterdNames) {
        this.mMerchantsList = filterdNames;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_merchant_name)
        TextView mTvMerchantName;
        @BindView(R.id.tv_merchant_external_id)
        TextView mTvMerchantExternalId;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}