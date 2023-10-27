package org.mifos.mobilewallet.mifospay.savedcards.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.mifospay.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a Adapter Class to display Cards.
 *
 * @author ankur
 * @since 21/May/2018
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<Card> cards;

    @Inject
    public CardsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvFname.setText(cards.get(position).getFirstName());
        holder.tvLname.setText(cards.get(position).getLastName());
        holder.tvCardNumber.setText(cards.get(position).getCardNumber());
        holder.tvExpirayDate.setText(cards.get(position).getExpiryDate());
    }

    @Override
    public int getItemCount() {
        if (cards != null) {
            return cards.size();
        } else {
            return 0;
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_fName)
        TextView tvFname;

        @BindView(R.id.tv_lName)
        TextView tvLname;

        @BindView(R.id.tv_card_number)
        TextView tvCardNumber;

        @BindView(R.id.tv_expiry_date)
        TextView tvExpirayDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
