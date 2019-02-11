package org.mifos.mobilewallet.mifospay.invoice.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.utils.DateHelper;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is the adapter class that will feed data to the RecyclerView.
 * @author ankur
 * @since 11/June/2018
 */

public class InvoicesAdapter
        extends RecyclerView.Adapter<InvoicesAdapter.ViewHolder> {

    private Context context;
    private List<Invoice> mInvoiceList;

    @Inject
    public InvoicesAdapter() {
    }

    /**
     * This function creates the viewholder and initializes/inflates the view with the given layout.
     * @param parent This is the parent view.
     * @param viewType This is the viewtype.
     * @return This returns the viewholder after inflation.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_invoice, parent, false);
        return new ViewHolder(v);
    }

    /**
     * This function displays the data at the specified position.
     * @param holder This is the viewholder.
     * @param position This is the position of the view.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Invoice invoice = mInvoiceList.get(position);

        holder.mTvInvoiceId.setText("Invoice Id: " + invoice.getId() + "");
        holder.mTvInvoiceStatus.setText(
                (invoice.getStatus() == 0) ? Constants.PENDING : Constants.DONE);
        holder.mTvInvoiceDate.setText(DateHelper.getDateAsString(invoice.getDate()));
        holder.mTvInvoiceAmount.setText(Constants.INR + invoice.getAmount());
    }

    /**
     * This function returns the item count of the invoice list.
     * @return Item count of the invoice list.
     */
    @Override
    public int getItemCount() {
        if (mInvoiceList != null) {
            return mInvoiceList.size();
        } else {
            return 0;
        }
    }

    /**
     * This function sets the context.
     * @param context This is the context that is set.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * This function sets the data to the invoice list.
     * @param invoices This is the new list whose data is set.
     */
    public void setData(List<Invoice> invoices) {
        this.mInvoiceList = invoices;
        notifyDataSetChanged();
    }

    /**
     * This function returns the Invoice List.
     * @return Invoice list of the type Invoice.
     */
    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    /**
     * This is the Viewholder class which defines all the components
     * to be feeded in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_invoice_id)
        TextView mTvInvoiceId;
        @BindView(R.id.tv_invoice_date)
        TextView mTvInvoiceDate;
        @BindView(R.id.tv_invoice_status)
        TextView mTvInvoiceStatus;
        @BindView(R.id.tv_invoice_amount)
        TextView mTvInvoiceAmount;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}


