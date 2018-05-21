package org.mifos.mobilewallet.savedcards.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.core.domain.model.Card;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 21/May/2018
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cards;

    @Inject
    public CardsAdapter() {

    }

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_card, parent, false);
        return new CardsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCardNumber.setText(cards.get(position).getCardNumber());
        holder.tvExpiryDate.setText("expiry date");
        holder.tvCVV.setText(cards.get(position).getCvv());
    }

    @Override
    public int getItemCount() {
        if (cards != null) {
            return cards.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_card_number)
        TextView tvCardNumber;

        @BindView(R.id.tv_cvv)
        TextView tvCVV;

        @BindView(R.id.tv_expiry_date)
        TextView tvExpiryDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
