package org.mifos.mobilewallet.mifospay.bank.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.domain.model.Bank;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 20/6/17.
 */

public class OtherBankAdapter extends RecyclerView.Adapter<OtherBankAdapter.ViewHolder> implements
        Filterable {

    private Context context;
    private List<Bank> otherBanks;
    private List<Bank> filteredBanks;

    @Inject
    public OtherBankAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_banks, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivPopularBank.setImageDrawable(
                ContextCompat.getDrawable(context, otherBanks.get(position).getImage()));
        holder.tvPopularBank.setText(otherBanks.get(position).getName());

    }

    @Override
    public int getItemCount() {
        if (otherBanks != null) {
            return otherBanks.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<Bank> banks) {
        this.otherBanks = banks;
        notifyDataSetChanged();
    }

    public Bank getBank(int position) {
        return otherBanks.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredBanks = otherBanks;
                } else {
                    List<Bank> filteredList = new ArrayList<>();
                    for (Bank bank : otherBanks) {
                        if (bank.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(bank);
                        }
                    }
                    filteredBanks = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredBanks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredBanks = (List<Bank>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filterList(List<Bank> filterdNames) {
        this.otherBanks = filterdNames;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_popular_bank)
        ImageView ivPopularBank;

        @BindView(R.id.tv_popular_bank)
        TextView tvPopularBank;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}