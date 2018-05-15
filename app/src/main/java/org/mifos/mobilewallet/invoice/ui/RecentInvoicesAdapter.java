package org.mifos.mobilewallet.invoice.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 11/7/17.
 */

public class RecentInvoicesAdapter extends RecyclerView.Adapter<RecentInvoicesAdapter.ViewHolder> {

    private Context context;
    private List<Invoice> invoices;

    @Inject
    public RecentInvoicesAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_invoice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvInvoiceAmount.setText(Constants.RUPEE + invoices.get(position).getAmount());
        holder.tvInvoiceId.setText("Invoice ID : " +
                invoices.get(position).getInvoiceId());
        holder.tvInvoiceDate.setText(invoices.get(position).getDate());
        if (invoices.get(position).getStatus() == 0) {
            holder.tvInvoiceStatus.setText("Pending");
            holder.ivInvoiceStatus.setImageResource(R.drawable.ic_invoice_pending);
        } else {
            holder.tvInvoiceStatus.setText("Paid");
            holder.ivInvoiceStatus.setImageResource(R.drawable.ic_invoice_paid);
        }
    }

    @Override
    public int getItemCount() {
        if (invoices != null) {
            return invoices.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_invoice_id)
        TextView tvInvoiceId;

        @BindView(R.id.tv_invoice_status)
        TextView tvInvoiceStatus;

        @BindView(R.id.tv_invoice_amount)
        TextView tvInvoiceAmount;

        @BindView(R.id.tv_invoice_date)
        TextView tvInvoiceDate;

        @BindView(R.id.iv_invoice_status)
        ImageView ivInvoiceStatus;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setData(List<Invoice> invoices) {
        this.invoices = invoices;
        notifyDataSetChanged();
    }

    public Invoice getInvoice(int position) {
        return invoices.get(position);
    }
}

