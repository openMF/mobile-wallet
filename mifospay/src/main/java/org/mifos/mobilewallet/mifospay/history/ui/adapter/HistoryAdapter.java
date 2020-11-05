package org.mifos.mobilewallet.mifospay.history.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.Account;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Utils;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mifos.mobilewallet.mifospay.utils.Constants.CREDIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.DEBIT;
import static org.mifos.mobilewallet.mifospay.utils.Constants.OTHER;
import static org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance;

/**
 * Created by naman on 17/8/17.
 */

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<JournalEntry> transactions;
    private String currency;
    private Context context;
    private String accountIdentifier;

    @Inject
    public HistoryAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_casual_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JournalEntry entry = transactions.get(position);

        List<Account> creditors = entry.getCreditors();
        // default value
        Double balance = 0.0;
        if (creditors.get(0).getAmount() != null) {
            balance = Double.valueOf(creditors.get(0).getAmount());
        }
        // Default as DEBIT
        String transactionType = DEBIT;
        if (accountIdentifier.equals(creditors.get(0).getAccountNumber())) {
            transactionType = CREDIT;
        }
        holder.tvTransactionAmount
                .setText(getFormattedAccountBalance(balance, currency));
        String formattedDate = Utils.getFormattedDate(entry.getTransactionDate());
        holder.tvTransactionDate.setText(formattedDate);

        if (balance > 0 && context != null) {
            int color = ContextCompat.getColor(context, R.color.colorAccentBlue);
            holder.tvTransactionAmount.setTextColor(color);
        }

        switch (transactionType) {
            case DEBIT:
                holder.tvTransactionStatus.setText(DEBIT);
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText(CREDIT);
                break;
            case OTHER:
                holder.tvTransactionStatus.setText(OTHER);
                break;
        }
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

    public void setData(List<JournalEntry> transactions, String currency,
                        String accountIdentifier) {
        this.currency = currency;
        this.transactions = transactions;
        this.accountIdentifier = accountIdentifier;
        notifyDataSetChanged();
    }

    public ArrayList<JournalEntry> getTransactions() {
        return (ArrayList<JournalEntry>) transactions;
    }

    public JournalEntry getTransaction(int position) {
        return transactions.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_casual_list_title)
        TextView tvTransactionStatus;

        @BindView(R.id.tv_item_casual_list_optional_caption)
        TextView tvTransactionAmount;

        @BindView(R.id.tv_item_casual_list_subtitle)
        TextView tvTransactionDate;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}

