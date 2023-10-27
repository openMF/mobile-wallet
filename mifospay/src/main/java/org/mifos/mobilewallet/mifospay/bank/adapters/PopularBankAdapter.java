package org.mifos.mobilewallet.mifospay.bank.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.domain.model.Bank;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 20/6/17.
 */

public class PopularBankAdapter extends RecyclerView.Adapter<PopularBankAdapter.ViewHolder> {

    private Context context;
    private List<Bank> popularBanks;

    @Inject
    public PopularBankAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_banks,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivPopularBank.setImageDrawable(ContextCompat
                .getDrawable(context, popularBanks.get(position).getImage()));
        holder.tvPopularBank.setText(popularBanks.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (popularBanks != null) {
            return popularBanks.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<Bank> banks) {
        this.popularBanks = banks;
        notifyDataSetChanged();
    }

    public Bank getBank(int position) {
        return popularBanks.get(position);
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