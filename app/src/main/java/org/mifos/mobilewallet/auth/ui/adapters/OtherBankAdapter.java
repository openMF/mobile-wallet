package org.mifos.mobilewallet.auth.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.domain.model.Bank;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 20/6/17.
 */

public class OtherBankAdapter extends RecyclerView.Adapter<OtherBankAdapter.ViewHolder> {

    private Context context;
    private List<Bank> otherBanks;

    @Inject
    public OtherBankAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_other_banks, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivPopularBank.setImageDrawable(
                ContextCompat.getDrawable(context, otherBanks.get(position).getImage()));
        holder.tvPopularBank.setText(otherBanks.get(position).getName());

        if (!otherBanks.get(position).getName().contains("RBL")) {
            holder.tvPopularBank.setTextColor(Color.LTGRAY);
        }
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

    public void setData(List<Bank> banks) {
        this.otherBanks = banks;
        notifyDataSetChanged();
    }

    public Bank getBank(int position) {
        return otherBanks.get(position);
    }
}