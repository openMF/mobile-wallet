package org.mifos.mobilewallet.mifospay.invoice.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.utils.DateHelper;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.ListItemOnClick;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 11/June/2018
 */

public class InvoicesAdapter
        extends RecyclerView.Adapter<InvoicesAdapter.ViewHolder> {

    private Context context;
    private List<Invoice> mInvoiceList;
    private final ListItemOnClick onClickListener;

    public InvoicesAdapter(ListItemOnClick onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.invoice_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(v1 -> onClickListener.onClick(vh.getBindingAdapterPosition()));
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Invoice invoice = mInvoiceList.get(position);
        holder.mTvInvoiceTitle.setText(invoice.getTitle());
        holder.mTvInvoiceId.setText("Invoice Id: " + invoice.getId() + "");
        holder.mTvInvoiceStatus.setText(
                (invoice.getStatus() == 0) ? Constants.PENDING : Constants.DONE);
        holder.mInvoiceImage.setImageResource(
                (invoice.getStatus() == 0) ? R.drawable.ic_remove_circle_outline_black_24dp :
                        R.drawable.ic_check_round_black_24dp);
        holder.mTvInvoiceDate.setText(DateHelper.getDateAsString(invoice.getDate()));
        holder.mTvInvoiceAmount.setText(Constants.INR + invoice.getAmount());
    }

    @Override
    public int getItemCount() {
        if (mInvoiceList != null) {
            return mInvoiceList.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<Invoice> invoices) {
        this.mInvoiceList = invoices;
        notifyDataSetChanged();
    }

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_invoice_id)
        TextView mTvInvoiceId;
        @BindView(R.id.invoice_status_image)
        ImageView mInvoiceImage;
        @BindView(R.id.tv_invoice_date)
        TextView mTvInvoiceDate;
        @BindView(R.id.tv_invoice_status)
        TextView mTvInvoiceStatus;
        @BindView(R.id.tv_invoice_amount)
        TextView mTvInvoiceAmount;
        @BindView(R.id.tv_invoice_title)
        TextView mTvInvoiceTitle;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}


